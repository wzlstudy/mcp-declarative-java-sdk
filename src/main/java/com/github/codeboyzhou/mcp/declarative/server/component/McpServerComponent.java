package com.github.codeboyzhou.mcp.declarative.server.component;

import java.lang.reflect.Method;

public interface McpServerComponent<T> {
  T create(Method method);
}
