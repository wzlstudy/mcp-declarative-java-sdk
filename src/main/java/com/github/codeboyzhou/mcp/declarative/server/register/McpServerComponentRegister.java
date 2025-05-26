package com.github.codeboyzhou.mcp.declarative.server.register;

import java.lang.reflect.Method;

public interface McpServerComponentRegister<T, R> {

    void registerTo(T server);

    R createComponentFrom(Class<?> clazz, Method method);
}
