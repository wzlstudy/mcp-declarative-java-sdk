package com.github.codeboyzhou.mcp.declarative.server.factory.configurable;

import com.github.codeboyzhou.mcp.declarative.configuration.McpServerConfiguration;
import com.github.codeboyzhou.mcp.declarative.configuration.McpServerStreamable;
import com.github.codeboyzhou.mcp.declarative.server.McpHttpServer;
import com.github.codeboyzhou.mcp.declarative.util.ObjectMappers;
import com.google.inject.Injector;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.transport.HttpServletStreamableServerTransportProvider;
import java.time.Duration;

public class ConfigurableMcpStreamableServerFactory extends AbstractConfigurableMcpServerFactory {

  public ConfigurableMcpStreamableServerFactory(Injector injector, McpServerConfiguration config) {
    super(injector, config);
  }

  public static ConfigurableMcpStreamableServerFactory of(Injector i, McpServerConfiguration c) {
    return new ConfigurableMcpStreamableServerFactory(i, c);
  }

  @Override
  public McpServer.SyncSpecification<?> sync() {
    McpServerStreamable streamable = config.streamable();
    HttpServletStreamableServerTransportProvider transportProvider =
        HttpServletStreamableServerTransportProvider.builder()
            .objectMapper(ObjectMappers.JSON_MAPPER)
            .mcpEndpoint(streamable.mcpEndpoint())
            .disallowDelete(streamable.disallowDelete())
            .keepAliveInterval(Duration.ofMillis(streamable.keepAliveInterval()))
            .build();
    McpHttpServer httpserver = new McpHttpServer();
    threadPool.execute(() -> httpserver.use(transportProvider).bind(streamable.port()).start());
    return McpServer.sync(transportProvider);
  }
}
