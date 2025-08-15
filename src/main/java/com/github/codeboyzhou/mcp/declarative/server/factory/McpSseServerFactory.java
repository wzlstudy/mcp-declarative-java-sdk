package com.github.codeboyzhou.mcp.declarative.server.factory;

import com.github.codeboyzhou.mcp.declarative.server.McpHttpServer;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.transport.HttpServletSseServerTransportProvider;

public class McpSseServerFactory extends AbstractMcpServerFactory<McpSseServerInfo> {
  @Override
  public McpServer.SyncSpecification<?> sync(McpSseServerInfo info) {
    HttpServletSseServerTransportProvider transportProvider =
        HttpServletSseServerTransportProvider.builder()
            .baseUrl(info.baseUrl())
            .sseEndpoint(info.sseEndpoint())
            .messageEndpoint(info.messageEndpoint())
            .build();
    threadPool.execute(() -> new McpHttpServer().use(transportProvider).bind(info.port()).start());
    return McpServer.sync(transportProvider);
  }
}
