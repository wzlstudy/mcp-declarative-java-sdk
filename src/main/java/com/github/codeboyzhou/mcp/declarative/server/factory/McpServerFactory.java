package com.github.codeboyzhou.mcp.declarative.server.factory;

import com.github.codeboyzhou.mcp.declarative.server.McpServerInfo;
import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpServerTransportProvider;

public interface McpServerFactory<T> {

    T create(McpServerInfo serverInfo, McpServerTransportProvider transportProvider);

    default McpSchema.ServerCapabilities configureServerCapabilities() {
        return McpSchema.ServerCapabilities.builder()
            .resources(true, true)
            .prompts(true)
            .tools(true)
            .build();
    }

}
