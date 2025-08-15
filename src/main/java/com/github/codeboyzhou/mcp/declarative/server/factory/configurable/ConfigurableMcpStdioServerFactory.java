package com.github.codeboyzhou.mcp.declarative.server.factory.configurable;

import com.github.codeboyzhou.mcp.declarative.configuration.McpServerConfiguration;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.transport.StdioServerTransportProvider;

public class ConfigurableMcpStdioServerFactory extends AbstractConfigurableMcpServerFactory {

  public ConfigurableMcpStdioServerFactory(McpServerConfiguration configuration) {
    super(configuration);
  }

  @Override
  public McpServer.SyncSpecification<?> specification() {
    StdioServerTransportProvider transportProvider = new StdioServerTransportProvider();
    return McpServer.sync(transportProvider);
  }
}
