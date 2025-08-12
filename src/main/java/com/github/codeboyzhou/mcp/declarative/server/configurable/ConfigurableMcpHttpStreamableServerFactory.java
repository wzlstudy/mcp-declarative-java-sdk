package com.github.codeboyzhou.mcp.declarative.server.configurable;

import com.github.codeboyzhou.mcp.declarative.common.NamedThreadFactory;
import com.github.codeboyzhou.mcp.declarative.configuration.McpServerConfiguration;
import com.github.codeboyzhou.mcp.declarative.server.McpHttpServer;
import io.modelcontextprotocol.server.McpAsyncServer;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.transport.HttpServletStreamableServerTransportProvider;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
  public McpAsyncServer create() {
    HttpServletStreamableServerTransportProvider transportProvider = transportProvider();
    McpAsyncServer server =
        McpServer.async(transportProvider)
            .serverInfo(configuration.name(), configuration.version())
            .capabilities(serverCapabilities())
            .instructions(configuration.instructions())
            .requestTimeout(Duration.ofMillis(configuration.requestTimeout()))
            .build();
    McpHttpServer httpServer = new McpHttpServer();
    NamedThreadFactory threadFactory = new NamedThreadFactory(McpHttpServer.class.getSimpleName());
    ExecutorService executor = Executors.newSingleThreadExecutor(threadFactory);
    executor.execute(
        () -> httpServer.use(transportProvider).bind(configuration.sse().port()).start());
    return server;
  }
}
