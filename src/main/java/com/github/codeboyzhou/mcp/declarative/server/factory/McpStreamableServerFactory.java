package com.github.codeboyzhou.mcp.declarative.server.factory;

import com.github.codeboyzhou.mcp.declarative.server.McpHttpServer;
import com.github.codeboyzhou.mcp.declarative.util.ObjectMappers;
import com.google.inject.Injector;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.transport.HttpServletStreamableServerTransportProvider;

public class McpStreamableServerFactory extends AbstractMcpServerFactory<McpStreamableServerInfo> {
  protected McpStreamableServerFactory(Injector injector) {
    super(injector);
  }

  public static McpStreamableServerFactory from(Injector injector) {
    return new McpStreamableServerFactory(injector);
  }

  @Override
  public McpServer.SyncSpecification<?> sync(McpStreamableServerInfo info) {
    HttpServletStreamableServerTransportProvider transportProvider =
        HttpServletStreamableServerTransportProvider.builder()
            .objectMapper(ObjectMappers.JSON_MAPPER)
            .mcpEndpoint(info.mcpEndpoint())
            .disallowDelete(info.disallowDelete())
            .contextExtractor(info.contextExtractor())
            .keepAliveInterval(info.keepAliveInterval())
            .build();
    McpHttpServer httpserver = new McpHttpServer();
    threadPool.execute(() -> httpserver.use(transportProvider).bind(info.port()).start());
    return McpServer.sync(transportProvider);
  }
}
