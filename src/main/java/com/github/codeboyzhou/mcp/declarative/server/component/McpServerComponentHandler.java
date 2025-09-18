package com.github.codeboyzhou.mcp.declarative.server.component;

import io.modelcontextprotocol.server.McpSyncServerExchange;
import java.lang.reflect.Method;

public interface McpServerComponentHandler<U, R> {
  R invoke(Method method, String description, McpSyncServerExchange exchange, U request);
}
