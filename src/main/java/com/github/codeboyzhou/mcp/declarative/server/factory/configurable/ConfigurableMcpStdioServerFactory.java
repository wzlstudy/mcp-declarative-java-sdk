package com.github.codeboyzhou.mcp.declarative.server.factory.configurable;

import com.github.codeboyzhou.mcp.declarative.configuration.McpServerConfiguration;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.transport.StdioServerTransportProvider;

public class ConfigurableMcpStdioServerFactory extends AbstractConfigurableMcpServerFactory {

  public ConfigurableMcpStdioServerFactory(McpServerConfiguration configuration) {
    super(configuration);
  }

  public static ConfigurableMcpStdioServerFactory of(McpServerConfiguration configuration) {
    return new ConfigurableMcpStdioServerFactory(configuration);
  }

  @Override
  public McpServer.SyncSpecification<?> sync() {
    StdioServerTransportProvider transportProvider = new StdioServerTransportProvider();
    return McpServer.sync(transportProvider);
  }
}
