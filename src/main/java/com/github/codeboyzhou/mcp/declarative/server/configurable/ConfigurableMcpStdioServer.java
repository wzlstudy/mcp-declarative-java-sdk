package com.github.codeboyzhou.mcp.declarative.server.configurable;

import com.github.codeboyzhou.mcp.declarative.configuration.McpServerConfiguration;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.transport.StdioServerTransportProvider;

public class ConfigurableMcpStdioServer extends AbstractConfigurableMcpServer {

  public ConfigurableMcpStdioServer(McpServerConfiguration configuration) {
    super(configuration);
  }

  @Override
  public McpServer.SyncSpecification<?> sync() {
    StdioServerTransportProvider transportProvider = new StdioServerTransportProvider();
    return McpServer.sync(transportProvider);
  }
}
