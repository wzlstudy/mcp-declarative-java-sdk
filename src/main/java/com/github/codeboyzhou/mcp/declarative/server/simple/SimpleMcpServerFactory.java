package com.github.codeboyzhou.mcp.declarative.server.simple;

import io.modelcontextprotocol.server.McpAsyncServer;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpServerTransportProviderBase;

public interface SimpleMcpServerFactory<
    T extends McpServerTransportProviderBase, S extends SimpleMcpServerBaseInfo> {

  default McpSchema.ServerCapabilities serverCapabilities() {
    return McpSchema.ServerCapabilities.builder()
        .resources(true, true)
        .prompts(true)
        .tools(true)
        .build();
  }

  default McpAsyncServer createServer(S serverInfo) {
    return specification(serverInfo)
        .serverInfo(serverInfo.name(), serverInfo.version())
        .capabilities(serverCapabilities())
        .instructions(serverInfo.instructions())
        .requestTimeout(serverInfo.requestTimeout())
        .build();
  }

  McpServer.AsyncSpecification<?> specification(S serverInfo);

  T transportProvider(S serverInfo);
}
