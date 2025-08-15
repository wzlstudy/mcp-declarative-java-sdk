package com.github.codeboyzhou.mcp.declarative.server.factory.configurable;

import com.github.codeboyzhou.mcp.declarative.configuration.McpServerConfiguration;
import com.github.codeboyzhou.mcp.declarative.configuration.McpServerSSE;
import com.github.codeboyzhou.mcp.declarative.server.McpHttpServer;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.transport.HttpServletSseServerTransportProvider;

public class ConfigurableMcpSseServerFactory extends AbstractConfigurableMcpServerFactory {

  public ConfigurableMcpSseServerFactory(McpServerConfiguration configuration) {
    super(configuration);
  }

  @Override
  public McpServer.SyncSpecification<?> specification() {
    McpServerSSE sse = configuration.sse();
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
