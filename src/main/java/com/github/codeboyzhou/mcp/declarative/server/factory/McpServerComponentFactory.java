package com.github.codeboyzhou.mcp.declarative.server.factory;

import io.modelcontextprotocol.server.McpSyncServer;

import java.lang.reflect.Method;

public interface McpServerComponentFactory<T> {

    T create(Class<?> clazz, Method method);

    void registerTo(McpSyncServer server);

}
