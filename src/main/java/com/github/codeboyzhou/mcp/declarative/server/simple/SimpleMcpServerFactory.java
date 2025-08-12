package com.github.codeboyzhou.mcp.declarative.server.simple;

import io.modelcontextprotocol.server.McpAsyncServer;
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

  T transportProvider(S serverInfo);

  McpAsyncServer create(S serverInfo);
}
