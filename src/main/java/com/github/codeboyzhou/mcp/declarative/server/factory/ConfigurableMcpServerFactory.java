package com.github.codeboyzhou.mcp.declarative.server.factory;

import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpServerTransportProvider;

public interface ConfigurableMcpServerFactory<T> {

    T create();

    McpServerTransportProvider transportProvider();

    McpSchema.ServerCapabilities configureServerCapabilities();

}
