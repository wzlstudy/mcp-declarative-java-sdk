package com.github.codeboyzhou.mcp.declarative.server.factory;

import com.github.codeboyzhou.mcp.declarative.server.McpServerInfo;
import com.google.inject.Injector;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.transport.StdioServerTransportProvider;

public class McpStdioServerFactory extends AbstractMcpServerFactory<McpServerInfo> {
  protected McpStdioServerFactory(Injector injector) {
    super(injector);
  }

  public static McpStdioServerFactory of(Injector injector) {
    return new McpStdioServerFactory(injector);
  }

  @Override
  public McpServer.SyncSpecification<?> sync(McpServerInfo serverInfo) {
    return McpServer.sync(new StdioServerTransportProvider());
  }
}
