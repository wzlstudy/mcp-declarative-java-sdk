package com.github.codeboyzhou.mcp.declarative.server.factory;

import com.github.codeboyzhou.mcp.declarative.common.NamedThreadFactory;
import com.github.codeboyzhou.mcp.declarative.configuration.McpServerConfiguration;
import com.github.codeboyzhou.mcp.declarative.configuration.McpServerSSE;
import com.github.codeboyzhou.mcp.declarative.server.McpHttpServer;
import io.modelcontextprotocol.server.McpAsyncServer;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.transport.HttpServletSseServerTransportProvider;
import java.time.Duration;
import java.util.concurrent.Executors;

public class ConfigurableMcpHttpSseServerFactory
    extends AbstractConfigurableMcpServerFactory<HttpServletSseServerTransportProvider> {

  public ConfigurableMcpHttpSseServerFactory(McpServerConfiguration configuration) {
    super(configuration);
  }

  @Override
  public HttpServletSseServerTransportProvider transportProvider() {
    McpServerSSE sse = configuration.sse();
    final String baseUrl = sse.baseUrl();
    final String messageEndpoint = sse.messageEndpoint();
    final String sseEndpoint = sse.endpoint();
    return HttpServletSseServerTransportProvider.builder()
        .baseUrl(baseUrl)
        .sseEndpoint(sseEndpoint)
        .messageEndpoint(messageEndpoint)
        .build();
  }

  @Override
  public McpAsyncServer create() {
    HttpServletSseServerTransportProvider transportProvider = transportProvider();
    McpAsyncServer server =
        McpServer.async(transportProvider)
            .serverInfo(configuration.name(), configuration.version())
            .capabilities(serverCapabilities())
            .instructions(configuration.instructions())
            .requestTimeout(Duration.ofMillis(configuration.requestTimeout()))
            .build();
    McpHttpServer httpServer = new McpHttpServer(transportProvider, configuration.sse().port());
    NamedThreadFactory threadFactory = new NamedThreadFactory(McpHttpServer.class.getSimpleName());
    Executors.newSingleThreadExecutor(threadFactory).execute(httpServer::start);
    return server;
  }
}
