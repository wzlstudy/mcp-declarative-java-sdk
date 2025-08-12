package com.github.codeboyzhou.mcp.declarative.server.simple;

import com.github.codeboyzhou.mcp.declarative.common.NamedThreadFactory;
import com.github.codeboyzhou.mcp.declarative.server.McpHttpServer;
import io.modelcontextprotocol.server.McpAsyncServer;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.transport.HttpServletSseServerTransportProvider;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SimpleMcpHttpSseServerFactory
    implements SimpleMcpServerFactory<
        HttpServletSseServerTransportProvider, SimpleMcpHttpSseServerInfo> {

  @Override
  public HttpServletSseServerTransportProvider transportProvider(SimpleMcpHttpSseServerInfo info) {
    return HttpServletSseServerTransportProvider.builder()
        .baseUrl(info.baseUrl())
        .sseEndpoint(info.sseEndpoint())
        .messageEndpoint(info.messageEndpoint())
        .build();
  }

  @Override
  public McpAsyncServer create(SimpleMcpHttpSseServerInfo info) {
    HttpServletSseServerTransportProvider transportProvider = transportProvider(info);
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
