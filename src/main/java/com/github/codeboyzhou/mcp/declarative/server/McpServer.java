package com.github.codeboyzhou.mcp.declarative.server;

public interface McpServer<S extends McpServerInfo> {
  io.modelcontextprotocol.server.McpServer.SyncSpecification<?> sync(S serverInfo);
}
