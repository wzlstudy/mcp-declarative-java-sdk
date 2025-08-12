package com.github.codeboyzhou.mcp.declarative.server.configurable;

import com.github.codeboyzhou.mcp.declarative.configuration.McpServerConfiguration;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.transport.StdioServerTransportProvider;

public class ConfigurableMcpStdioServerFactory
    extends AbstractConfigurableMcpServerFactory<StdioServerTransportProvider> {

  public ConfigurableMcpStdioServerFactory(McpServerConfiguration configuration) {
    super(configuration);
  }

  @Override
  public StdioServerTransportProvider transportProvider() {
    return new StdioServerTransportProvider();
  }

  @Override
  public McpServer.AsyncSpecification<?> specification() {
    return McpServer.async(transportProvider());
  }
}
