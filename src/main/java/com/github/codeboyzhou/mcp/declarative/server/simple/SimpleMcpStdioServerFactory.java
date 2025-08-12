package com.github.codeboyzhou.mcp.declarative.server.simple;

import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.transport.StdioServerTransportProvider;

public class SimpleMcpStdioServerFactory
    implements SimpleMcpServerFactory<StdioServerTransportProvider, SimpleMcpServerBaseInfo> {

  @Override
  public McpServer.AsyncSpecification<?> specification(SimpleMcpServerBaseInfo serverInfo) {
    return McpServer.async(transportProvider(serverInfo));
  }

  @Override
  public StdioServerTransportProvider transportProvider(SimpleMcpServerBaseInfo serverInfo) {
    return new StdioServerTransportProvider();
  }
}
