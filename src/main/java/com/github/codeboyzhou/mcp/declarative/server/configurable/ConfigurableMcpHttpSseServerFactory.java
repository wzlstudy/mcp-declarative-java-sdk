package com.github.codeboyzhou.mcp.declarative.server.configurable;

import com.github.codeboyzhou.mcp.declarative.configuration.McpServerConfiguration;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.transport.HttpServletSseServerTransportProvider;

public class ConfigurableMcpHttpSseServerFactory
    extends AbstractConfigurableMcpServerFactory<HttpServletSseServerTransportProvider> {

  public ConfigurableMcpHttpSseServerFactory(McpServerConfiguration configuration) {
    super(configuration);
  }

  @Override
  public HttpServletSseServerTransportProvider transportProvider() {
    return HttpServletSseServerTransportProvider.builder()
        .baseUrl(configuration.sse().baseUrl())
        .sseEndpoint(configuration.sse().endpoint())
        .messageEndpoint(configuration.sse().messageEndpoint())
        .build();
  }

  @Override
  public McpServer.AsyncSpecification<?> specification() {
    return McpServer.async(transportProvider());
  }
}
