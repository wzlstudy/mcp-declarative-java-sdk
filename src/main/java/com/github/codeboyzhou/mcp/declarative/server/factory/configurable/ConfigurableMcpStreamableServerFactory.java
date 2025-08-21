package com.github.codeboyzhou.mcp.declarative.server.factory.configurable;

import com.github.codeboyzhou.mcp.declarative.configuration.McpServerConfiguration;
import com.github.codeboyzhou.mcp.declarative.configuration.McpServerStreamable;
import com.github.codeboyzhou.mcp.declarative.server.EmbeddedJettyServer;
import com.github.codeboyzhou.mcp.declarative.util.ObjectMappers;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.transport.HttpServletStreamableServerTransportProvider;
import java.time.Duration;

public class ConfigurableMcpStreamableServerFactory extends AbstractConfigurableMcpServerFactory {

  public ConfigurableMcpStreamableServerFactory(McpServerConfiguration configuration) {
    super(configuration);
  }

  @Override
  public McpServer.SyncSpecification<?> sync() {
    McpServerStreamable streamable = configuration.streamable();
    HttpServletStreamableServerTransportProvider transportProvider =
        HttpServletStreamableServerTransportProvider.builder()
            .objectMapper(ObjectMappers.JSON_MAPPER)
            .mcpEndpoint(streamable.mcpEndpoint())
            .disallowDelete(streamable.disallowDelete())
            .keepAliveInterval(Duration.ofMillis(streamable.keepAliveInterval()))
            .build();
    EmbeddedJettyServer httpserver = new EmbeddedJettyServer();
    httpserver.use(transportProvider).bind(streamable.port()).start();
    return McpServer.sync(transportProvider);
  }
}
