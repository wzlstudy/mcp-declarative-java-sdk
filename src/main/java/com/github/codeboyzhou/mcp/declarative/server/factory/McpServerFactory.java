package com.github.codeboyzhou.mcp.declarative.server.factory;

import com.github.codeboyzhou.mcp.declarative.server.McpServerInfo;
import io.modelcontextprotocol.server.McpAsyncServer;
import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpServerTransportProvider;

public interface McpServerFactory<T extends McpServerTransportProvider, S extends McpServerInfo> {

  T transportProvider(S serverInfo);

  McpAsyncServer create(S serverInfo);

  McpSchema.ServerCapabilities serverCapabilities();
}
