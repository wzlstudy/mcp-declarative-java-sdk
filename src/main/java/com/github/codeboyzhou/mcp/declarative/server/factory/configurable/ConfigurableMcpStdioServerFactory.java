package com.github.codeboyzhou.mcp.declarative.server.factory.configurable;

import com.github.codeboyzhou.mcp.declarative.configuration.McpServerConfiguration;
import com.google.inject.Injector;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.transport.StdioServerTransportProvider;

public class ConfigurableMcpStdioServerFactory extends AbstractConfigurableMcpServerFactory {

  public ConfigurableMcpStdioServerFactory(Injector injector, McpServerConfiguration config) {
    super(injector, config);
  }

  public static ConfigurableMcpStdioServerFactory of(Injector i, McpServerConfiguration c) {
    return new ConfigurableMcpStdioServerFactory(i, c);
  }

  @Override
  public McpServer.SyncSpecification<?> sync() {
    StdioServerTransportProvider transportProvider = new StdioServerTransportProvider();
    return McpServer.sync(transportProvider);
  }
}
