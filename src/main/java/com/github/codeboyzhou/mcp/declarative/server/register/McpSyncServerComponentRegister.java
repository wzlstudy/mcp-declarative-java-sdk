package com.github.codeboyzhou.mcp.declarative.server.register;

import com.google.inject.Injector;
import io.modelcontextprotocol.server.McpSyncServer;

public abstract class McpSyncServerComponentRegister<R> implements McpServerComponentRegister<McpSyncServer, R> {

    protected final Injector injector;

    protected McpSyncServerComponentRegister(Injector injector) {
        this.injector = injector;
    }

}
