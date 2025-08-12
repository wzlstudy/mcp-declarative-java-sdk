package com.github.codeboyzhou.mcp.declarative.server.simple;

import com.github.codeboyzhou.mcp.declarative.util.ObjectMappers;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.transport.HttpServletStreamableServerTransportProvider;

public class SimpleMcpHttpStreamableServerFactory
    implements SimpleMcpServerFactory<
        HttpServletStreamableServerTransportProvider, SimpleMcpHttpStreamableServerInfo> {

  @Override
  public McpServer.AsyncSpecification<?> specification(
      SimpleMcpHttpStreamableServerInfo serverInfo) {
    return McpServer.async(transportProvider(serverInfo));
  }

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
}
