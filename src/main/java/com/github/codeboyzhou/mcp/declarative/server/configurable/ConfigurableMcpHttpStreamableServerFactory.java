package com.github.codeboyzhou.mcp.declarative.server.configurable;

import com.github.codeboyzhou.mcp.declarative.configuration.McpServerConfiguration;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.transport.HttpServletStreamableServerTransportProvider;

public class ConfigurableMcpHttpStreamableServerFactory
    extends AbstractConfigurableMcpServerFactory<HttpServletStreamableServerTransportProvider> {

  public ConfigurableMcpHttpStreamableServerFactory(McpServerConfiguration configuration) {
    super(configuration);
  }

  @Override
  public HttpServletStreamableServerTransportProvider transportProvider() {
    return HttpServletStreamableServerTransportProvider.builder().build();
  }

  @Override
  public McpServer.AsyncSpecification<?> specification() {
    return McpServer.async(transportProvider());
  }
}
