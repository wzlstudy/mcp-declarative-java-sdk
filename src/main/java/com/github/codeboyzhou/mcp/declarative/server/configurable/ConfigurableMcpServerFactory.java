package com.github.codeboyzhou.mcp.declarative.server.configurable;

import io.modelcontextprotocol.server.McpAsyncServer;
import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpServerTransportProviderBase;

public interface ConfigurableMcpServerFactory<T extends McpServerTransportProviderBase> {

  McpSchema.ServerCapabilities serverCapabilities();

  T transportProvider();

  McpAsyncServer create();
}
