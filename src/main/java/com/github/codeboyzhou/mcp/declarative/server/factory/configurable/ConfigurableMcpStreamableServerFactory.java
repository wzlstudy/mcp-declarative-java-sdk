package com.github.codeboyzhou.mcp.declarative.server.factory.configurable;

import com.github.codeboyzhou.mcp.declarative.configuration.McpServerConfiguration;
import com.github.codeboyzhou.mcp.declarative.server.McpHttpServer;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.transport.HttpServletStreamableServerTransportProvider;

public class ConfigurableMcpStreamableServerFactory extends AbstractConfigurableMcpServerFactory {

  public ConfigurableMcpStreamableServerFactory(McpServerConfiguration configuration) {
    super(configuration);
  }

  @Override
  public McpServer.SyncSpecification<?> specification() {
    HttpServletStreamableServerTransportProvider transportProvider =
        HttpServletStreamableServerTransportProvider.builder().build();
    threadPool.execute(() -> new McpHttpServer().use(transportProvider).bind(8080).start());
    return McpServer.sync(transportProvider);
  }
}
