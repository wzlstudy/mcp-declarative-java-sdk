package com.github.codeboyzhou.mcp.declarative.server.configurable;

import io.modelcontextprotocol.server.McpServer;

public interface ConfigurableMcpServer {
  McpServer.SyncSpecification<?> sync();
}
