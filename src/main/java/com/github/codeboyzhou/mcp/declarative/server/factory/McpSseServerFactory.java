package com.github.codeboyzhou.mcp.declarative.server.factory;

import com.github.codeboyzhou.mcp.declarative.server.McpHttpServer;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.transport.HttpServletSseServerTransportProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class McpSseServerFactory extends AbstractMcpServerFactory<McpSseServerInfo> {

  private static final Logger log = LoggerFactory.getLogger(McpSseServerFactory.class);

  @Override
  public McpServer.SyncSpecification<?> sync(McpSseServerInfo info) {
    log.warn("HTTP SSE mode has been deprecated, recommend to use Stream HTTP server instead.");
    HttpServletSseServerTransportProvider transportProvider =
        HttpServletSseServerTransportProvider.builder()
            .baseUrl(info.baseUrl())
            .sseEndpoint(info.sseEndpoint())
            .messageEndpoint(info.messageEndpoint())
            .build();
    McpHttpServer httpserver = new McpHttpServer();
    httpserver.use(transportProvider).bind(info.port()).start();
    return McpServer.sync(transportProvider);
  }
}
