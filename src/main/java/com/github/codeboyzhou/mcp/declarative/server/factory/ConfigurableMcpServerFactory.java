package com.github.codeboyzhou.mcp.declarative.server.factory;

import io.modelcontextprotocol.server.McpAsyncServer;
import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpServerTransportProvider;

public interface ConfigurableMcpServerFactory<T extends McpServerTransportProvider> {

  T transportProvider();

  McpAsyncServer create();

  McpSchema.ServerCapabilities serverCapabilities();
}
