package com.github.codeboyzhou.mcp.declarative.server.factory;

import com.github.codeboyzhou.mcp.declarative.common.NamedThreadFactory;
import com.github.codeboyzhou.mcp.declarative.server.McpHttpServer;
import com.github.codeboyzhou.mcp.declarative.server.McpStreamableServerInfo;
import com.github.codeboyzhou.mcp.declarative.util.ObjectMappers;
import io.modelcontextprotocol.server.McpAsyncServer;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.transport.HttpServletStreamableServerTransportProvider;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class McpHttpStreamableServerFactory
    extends AbstractMcpServerFactory<
        HttpServletStreamableServerTransportProvider, McpStreamableServerInfo> {

  @Override
  public HttpServletStreamableServerTransportProvider transportProvider(
      McpStreamableServerInfo serverInfo) {
    return HttpServletStreamableServerTransportProvider.builder()
        .objectMapper(ObjectMappers.JSON_MAPPER)
        .mcpEndpoint(serverInfo.mcpEndpoint())
        .disallowDelete(serverInfo.disallowDelete())
        .contextExtractor(serverInfo.contextExtractor())
        .keepAliveInterval(serverInfo.keepAliveInterval())
        .build();
  }

  @Override
  public McpAsyncServer create(McpStreamableServerInfo serverInfo) {
    HttpServletStreamableServerTransportProvider transportProvider = transportProvider(serverInfo);
    McpAsyncServer server =
        McpServer.async(transportProvider)
            .serverInfo(serverInfo.name(), serverInfo.version())
            .capabilities(serverCapabilities())
            .instructions(serverInfo.instructions())
            .requestTimeout(serverInfo.requestTimeout())
            .build();
    McpHttpServer httpServer = new McpHttpServer();
    NamedThreadFactory threadFactory = new NamedThreadFactory(McpHttpServer.class.getSimpleName());
    ExecutorService executor = Executors.newSingleThreadExecutor(threadFactory);
    executor.execute(() -> httpServer.use(transportProvider).bind(serverInfo.port()).start());
    return server;
  }
}
