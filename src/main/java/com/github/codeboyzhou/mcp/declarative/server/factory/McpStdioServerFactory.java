package com.github.codeboyzhou.mcp.declarative.server.factory;

import com.github.codeboyzhou.mcp.declarative.server.McpServerInfo;
import io.modelcontextprotocol.server.McpAsyncServer;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.transport.StdioServerTransportProvider;
import io.modelcontextprotocol.spec.McpServerTransportProvider;

public class McpStdioServerFactory extends AbstractMcpServerFactory<McpServerTransportProvider, McpServerInfo> {

    @Override
    public McpServerTransportProvider transportProvider(McpServerInfo serverInfo) {
        return new StdioServerTransportProvider();
    }

    @Override
    public McpAsyncServer create(McpServerInfo serverInfo) {
        return McpServer.async(transportProvider(serverInfo))
            .serverInfo(serverInfo.name(), serverInfo.version())
            .capabilities(serverCapabilities())
            .instructions(serverInfo.instructions())
            .requestTimeout(serverInfo.requestTimeout())
            .build();
    }

}
