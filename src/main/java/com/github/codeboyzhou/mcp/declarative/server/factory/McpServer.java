package com.github.codeboyzhou.mcp.declarative.server.factory;

import com.github.codeboyzhou.mcp.declarative.server.McpServerInfo;

public interface McpServer<S extends McpServerInfo> {
  io.modelcontextprotocol.server.McpServer.SyncSpecification<?> sync(S serverInfo);
}
