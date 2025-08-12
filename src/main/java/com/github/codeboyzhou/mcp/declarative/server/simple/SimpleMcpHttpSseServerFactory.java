package com.github.codeboyzhou.mcp.declarative.server.simple;

import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.transport.HttpServletSseServerTransportProvider;

public class SimpleMcpHttpSseServerFactory
    implements SimpleMcpServerFactory<
        HttpServletSseServerTransportProvider, SimpleMcpHttpSseServerInfo> {

  @Override
  public McpServer.AsyncSpecification<?> specification(SimpleMcpHttpSseServerInfo serverInfo) {
    return McpServer.async(transportProvider(serverInfo));
  }

  @Override
  public HttpServletSseServerTransportProvider transportProvider(SimpleMcpHttpSseServerInfo info) {
    return HttpServletSseServerTransportProvider.builder()
        .baseUrl(info.baseUrl())
        .sseEndpoint(info.sseEndpoint())
        .messageEndpoint(info.messageEndpoint())
        .build();
  }
}
