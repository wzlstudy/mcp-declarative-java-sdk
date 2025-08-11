package com.github.codeboyzhou.mcp.declarative.server.factory;

import com.github.codeboyzhou.mcp.declarative.common.NamedThreadFactory;
import com.github.codeboyzhou.mcp.declarative.server.McpHttpServer;
import com.github.codeboyzhou.mcp.declarative.server.McpSseServerInfo;
import io.modelcontextprotocol.server.McpAsyncServer;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.transport.HttpServletSseServerTransportProvider;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class McpHttpSseServerFactory
    extends AbstractMcpServerFactory<HttpServletSseServerTransportProvider, McpSseServerInfo> {

  @Override
  public HttpServletSseServerTransportProvider transportProvider(McpSseServerInfo serverInfo) {
    final String baseUrl = serverInfo.baseUrl();
    final String messageEndpoint = serverInfo.messageEndpoint();
    final String sseEndpoint = serverInfo.sseEndpoint();
    return HttpServletSseServerTransportProvider.builder()
        .baseUrl(baseUrl)
        .sseEndpoint(sseEndpoint)
        .messageEndpoint(messageEndpoint)
        .build();
  }

  @Override
  public McpAsyncServer create(McpSseServerInfo serverInfo) {
    HttpServletSseServerTransportProvider transportProvider = transportProvider(serverInfo);
    McpAsyncServer server =
        McpServer.async(transportProvider)
            .serverInfo(serverInfo.name(), serverInfo.version())
            .capabilities(serverCapabilities())
            .instructions(serverInfo.instructions())
            .requestTimeout(serverInfo.requestTimeout())
            .build();
    McpHttpServer httpServer = new McpHttpServer(serverInfo.port());
    NamedThreadFactory threadFactory = new NamedThreadFactory(McpHttpServer.class.getSimpleName());
    ExecutorService executor = Executors.newSingleThreadExecutor(threadFactory);
    executor.execute(() -> httpServer.start(transportProvider));
    return server;
  }
}
