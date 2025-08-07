package com.github.codeboyzhou.mcp.declarative.server;

import io.modelcontextprotocol.server.transport.HttpServletSseServerTransportProvider;
import java.time.Duration;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class McpHttpServer {

  private static final Logger logger = LoggerFactory.getLogger(McpHttpServer.class);

  private static final String DEFAULT_SERVLET_CONTEXT_PATH = "/";

  private static final String DEFAULT_SERVLET_PATH = "/*";

  private final HttpServletSseServerTransportProvider transportProvider;

  private final int port;

  public McpHttpServer(HttpServletSseServerTransportProvider transportProvider, int port) {
    this.transportProvider = transportProvider;
    this.port = port;
  }

  public void start() {
    ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
    handler.setContextPath(DEFAULT_SERVLET_CONTEXT_PATH);

    ServletHolder servletHolder = new ServletHolder(transportProvider);
    handler.addServlet(servletHolder, DEFAULT_SERVLET_PATH);

    Server httpserver = new Server(port);
    httpserver.setHandler(handler);
    httpserver.setStopAtShutdown(true);
    httpserver.setStopTimeout(Duration.ofSeconds(5).getSeconds());

    try {
      httpserver.start();
      logger.info("Jetty-based HTTP server started on http://127.0.0.1:{}", port);

      // Add a shutdown hook to stop the HTTP server and MCP server gracefully
      addShutdownHook(httpserver);

      // Check if the server is running in test mode
      final boolean testing =
          Boolean.parseBoolean(System.getProperty("mcp.declarative.java.sdk.testing"));
      if (testing) {
        logger.debug(
            "Jetty-based HTTP server is running in test mode, not waiting for HTTP server to stop");
        httpserver.stop();
        return;
      }

      // Wait for the HTTP server to stop
      httpserver.join();
    } catch (Exception e) {
      logger.error("Error starting HTTP server on http://127.0.0.1:{}", port, e);
    }
  }

  private void addShutdownHook(Server httpserver) {
    Runtime.getRuntime()
        .addShutdownHook(
            new Thread(
                () -> {
                  try {
                    logger.info("Shutting down HTTP server and MCP server");
                    httpserver.stop();
                  } catch (Exception e) {
                    logger.error("Error stopping HTTP server and MCP server", e);
                  }
                }));
  }
}
