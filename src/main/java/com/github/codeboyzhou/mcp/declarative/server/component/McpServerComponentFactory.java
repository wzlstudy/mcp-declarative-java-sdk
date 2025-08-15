package com.github.codeboyzhou.mcp.declarative.server.component;

import io.modelcontextprotocol.server.McpSyncServer;
import java.lang.reflect.Method;

public interface McpServerComponentFactory<T> {

  T create(Class<?> clazz, Method method);

  void register(McpSyncServer server);
}
