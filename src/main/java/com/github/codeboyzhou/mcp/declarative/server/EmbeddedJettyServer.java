package com.github.codeboyzhou.mcp.declarative.server;

import com.github.codeboyzhou.mcp.declarative.common.NamedThreadFactory;
import jakarta.servlet.http.HttpServlet;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmbeddedJettyServer {

  private static final Logger log = LoggerFactory.getLogger(EmbeddedJettyServer.class);

  private static final String DEFAULT_SERVLET_CONTEXT_PATH = "/";

  private static final String DEFAULT_SERVLET_PATH = "/*";

  private final ExecutorService threadPool;

  private HttpServlet servlet;

  private int port;

  public EmbeddedJettyServer() {
    this.threadPool = Executors.newSingleThreadExecutor(new NamedThreadFactory("mcp-http-server"));
  }

  public EmbeddedJettyServer use(HttpServlet servlet) {
    this.servlet = servlet;
    return this;
  }

  public EmbeddedJettyServer bind(int port) {
    this.port = port;
    return this;
  }

  public void start() {
    ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
    handler.setContextPath(DEFAULT_SERVLET_CONTEXT_PATH);

    ServletHolder servletHolder = new ServletHolder(servlet);
    handler.addServlet(servletHolder, DEFAULT_SERVLET_PATH);

    Server httpserver = new Server(port);
    httpserver.setHandler(handler);
    httpserver.setStopAtShutdown(true);
    httpserver.setStopTimeout(Duration.ofSeconds(5).toMillis());

    try {
      httpserver.start();
      addShutdownHook(httpserver);
      log.info("Embedded Jetty server started on http://127.0.0.1:{}", port);
    } catch (Exception e) {
      log.error("Error starting embedded Jetty server on http://127.0.0.1:{}", port, e);
    }

    threadPool.submit(() -> await(httpserver));
  }

  private void await(Server httpserver) {
    try {
      httpserver.join();
    } catch (InterruptedException e) {
      log.error("Error joining embedded Jetty server", e);
    }
  }

  private void addShutdownHook(Server httpserver) {
    Runnable runnable = () -> shutdown(httpserver);
    Thread shutdownHookThread = new Thread(runnable);
    Runtime.getRuntime().addShutdownHook(shutdownHookThread);
  }

  private void shutdown(Server httpserver) {
    try {
      log.info("Shutting down embedded Jetty server");
      httpserver.stop();
      servlet.destroy();
      threadPool.shutdown();
    } catch (Exception e) {
      log.error("Error stopping Jetty server", e);
    }
  }
}
