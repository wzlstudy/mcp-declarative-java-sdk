package com.github.codeboyzhou.mcp.declarative.server.factory;

import com.github.codeboyzhou.mcp.declarative.server.McpServerInfo;
import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpServerTransportProvider;

public abstract class AbstractMcpServerFactory<T extends McpServerTransportProvider, S extends McpServerInfo> implements McpServerFactory<T, S> {

    protected McpSchema.ServerCapabilities serverCapabilities() {
        return McpSchema.ServerCapabilities.builder()
            .resources(true, true)
            .prompts(true)
            .tools(true)
            .build();
    }

}
