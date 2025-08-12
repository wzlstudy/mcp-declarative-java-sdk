package com.github.codeboyzhou.mcp.declarative.server.simple;

import io.modelcontextprotocol.server.McpAsyncServer;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.transport.StdioServerTransportProvider;

public class SimpleMcpStdioServerFactory
    implements SimpleMcpServerFactory<StdioServerTransportProvider, SimpleMcpServerBaseInfo> {

  @Override
  public StdioServerTransportProvider transportProvider(SimpleMcpServerBaseInfo serverInfo) {
    return new StdioServerTransportProvider();
  }

  @Override
  public McpAsyncServer create(SimpleMcpServerBaseInfo serverInfo) {
    return McpServer.async(transportProvider(serverInfo))
        .serverInfo(serverInfo.name(), serverInfo.version())
        .capabilities(serverCapabilities())
        .instructions(serverInfo.instructions())
        .requestTimeout(serverInfo.requestTimeout())
        .build();
  }
}
