package com.github.codeboyzhou.mcp.declarative.server.factory;

import io.modelcontextprotocol.server.McpAsyncServer;

import java.lang.reflect.Method;

public interface McpServerComponentFactory<T> {

    T create(Class<?> clazz, Method method);

    void registerTo(McpAsyncServer server);

}
