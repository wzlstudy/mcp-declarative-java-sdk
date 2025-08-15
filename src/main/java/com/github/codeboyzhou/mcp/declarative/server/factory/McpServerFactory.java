package com.github.codeboyzhou.mcp.declarative.server.factory;

import com.github.codeboyzhou.mcp.declarative.server.McpServerInfo;
import io.modelcontextprotocol.server.McpServer;

public interface McpServerFactory<S extends McpServerInfo> {
  McpServer.SyncSpecification<?> sync(S serverInfo);
}
