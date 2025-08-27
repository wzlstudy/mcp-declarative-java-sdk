package com.github.codeboyzhou.mcp.declarative.server;

import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.transport.HttpServletSseServerTransportProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class McpSseServer extends AbstractMcpServer<McpSseServerInfo> {

  private static final Logger log = LoggerFactory.getLogger(McpSseServer.class);

  @Override
  public McpServer.SyncSpecification<?> sync(McpSseServerInfo info) {
    log.warn("HTTP SSE mode has been deprecated, recommend to use Stream HTTP server instead.");
    HttpServletSseServerTransportProvider transportProvider =
        HttpServletSseServerTransportProvider.builder()
            .baseUrl(info.baseUrl())
            .sseEndpoint(info.sseEndpoint())
            .messageEndpoint(info.messageEndpoint())
            .build();
    EmbeddedJettyServer httpserver = new EmbeddedJettyServer();
    httpserver.use(transportProvider).bind(info.port()).start();
    return McpServer.sync(transportProvider);
  }
}
