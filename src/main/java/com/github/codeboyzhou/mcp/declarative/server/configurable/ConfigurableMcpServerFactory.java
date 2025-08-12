package com.github.codeboyzhou.mcp.declarative.server.configurable;

import io.modelcontextprotocol.server.McpAsyncServer;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpServerTransportProviderBase;

public interface ConfigurableMcpServerFactory<T extends McpServerTransportProviderBase> {

  T transportProvider();

  McpServer.AsyncSpecification<?> specification();

  McpSchema.ServerCapabilities serverCapabilities();

  McpAsyncServer create();
}
