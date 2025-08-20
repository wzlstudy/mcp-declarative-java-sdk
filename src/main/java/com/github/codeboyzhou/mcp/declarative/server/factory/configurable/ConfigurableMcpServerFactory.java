package com.github.codeboyzhou.mcp.declarative.server.factory.configurable;

import io.modelcontextprotocol.server.McpServer;

public interface ConfigurableMcpServerFactory {
  McpServer.SyncSpecification<?> sync();
}
