package com.github.codeboyzhou.mcp.declarative.server.factory;

import com.github.codeboyzhou.mcp.declarative.server.McpHttpServer;
import com.github.codeboyzhou.mcp.declarative.util.ObjectMappers;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.transport.HttpServletStreamableServerTransportProvider;

public class McpStreamableServerFactory extends AbstractMcpServerFactory<McpStreamableServerInfo> {
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
    threadPool.execute(() -> new McpHttpServer().use(transportProvider).bind(info.port()).start());
    return McpServer.sync(transportProvider);
  }
}
