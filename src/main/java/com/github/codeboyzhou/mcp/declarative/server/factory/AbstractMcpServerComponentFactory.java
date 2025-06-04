package com.github.codeboyzhou.mcp.declarative.server.factory;

import com.google.inject.Injector;

public abstract class AbstractMcpServerComponentFactory<T> implements McpServerComponentFactory<T> {

    protected final Injector injector;

    protected AbstractMcpServerComponentFactory(Injector injector) {
        this.injector = injector;
    }

}
