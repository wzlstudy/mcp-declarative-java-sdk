package com.github.codeboyzhou.mcp.declarative.server.simple;

import com.github.codeboyzhou.mcp.declarative.common.NamedThreadFactory;
import com.github.codeboyzhou.mcp.declarative.server.McpHttpServer;
import com.github.codeboyzhou.mcp.declarative.util.ObjectMappers;
import io.modelcontextprotocol.server.McpAsyncServer;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.transport.HttpServletStreamableServerTransportProvider;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SimpleMcpHttpStreamableServerFactory
    implements SimpleMcpServerFactory<
        HttpServletStreamableServerTransportProvider, SimpleMcpHttpStreamableServerInfo> {

  @Override
  public HttpServletStreamableServerTransportProvider transportProvider(
      SimpleMcpHttpStreamableServerInfo info) {
    return HttpServletStreamableServerTransportProvider.builder()
        .objectMapper(ObjectMappers.JSON_MAPPER)
        .mcpEndpoint(info.mcpEndpoint())
        .disallowDelete(info.disallowDelete())
        .contextExtractor(info.contextExtractor())
        .keepAliveInterval(info.keepAliveInterval())
        .build();
  }

  @Override
  public McpAsyncServer create(SimpleMcpHttpStreamableServerInfo info) {
    HttpServletStreamableServerTransportProvider transportProvider = transportProvider(info);
    McpAsyncServer server =
        McpServer.async(transportProvider)
            .serverInfo(info.name(), info.version())
            .capabilities(serverCapabilities())
            .instructions(info.instructions())
            .requestTimeout(info.requestTimeout())
            .build();
    McpHttpServer httpServer = new McpHttpServer();
    NamedThreadFactory threadFactory = new NamedThreadFactory(McpHttpServer.class.getSimpleName());
    ExecutorService executor = Executors.newSingleThreadExecutor(threadFactory);
    executor.execute(() -> httpServer.use(transportProvider).bind(info.port()).start());
    return server;
  }
}
