package com.github.codeboyzhou.mcp.declarative.server.factory.configurable;

import com.github.codeboyzhou.mcp.declarative.configuration.McpServerConfiguration;
import com.github.codeboyzhou.mcp.declarative.configuration.McpServerSSE;
import com.github.codeboyzhou.mcp.declarative.server.McpHttpServer;
import com.google.inject.Injector;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.transport.HttpServletSseServerTransportProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigurableMcpSseServerFactory extends AbstractConfigurableMcpServerFactory {

  private static final Logger log = LoggerFactory.getLogger(ConfigurableMcpSseServerFactory.class);

  public ConfigurableMcpSseServerFactory(Injector injector, McpServerConfiguration config) {
    super(injector, config);
  }

  public static ConfigurableMcpSseServerFactory of(Injector i, McpServerConfiguration c) {
    return new ConfigurableMcpSseServerFactory(i, c);
  }

  @Override
  public McpServer.SyncSpecification<?> sync() {
    log.warn("HTTP SSE mode has been deprecated, recommend to use Stream HTTP server instead.");
    McpServerSSE sse = config.sse();
    HttpServletSseServerTransportProvider transportProvider =
        HttpServletSseServerTransportProvider.builder()
            .baseUrl(sse.baseUrl())
            .sseEndpoint(sse.endpoint())
            .messageEndpoint(sse.messageEndpoint())
            .build();
    McpHttpServer httpserver = new McpHttpServer();
    threadPool.execute(() -> httpserver.use(transportProvider).bind(sse.port()).start());
    return McpServer.sync(transportProvider);
  }
}
