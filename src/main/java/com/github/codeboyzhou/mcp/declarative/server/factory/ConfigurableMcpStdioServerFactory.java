package com.github.codeboyzhou.mcp.declarative.server.factory;

import com.github.codeboyzhou.mcp.declarative.configuration.McpServerConfiguration;
import io.modelcontextprotocol.server.McpAsyncServer;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.transport.StdioServerTransportProvider;
import io.modelcontextprotocol.spec.McpServerTransportProvider;

import java.time.Duration;

public class ConfigurableMcpStdioServerFactory extends AbstractConfigurableMcpServerFactory<McpServerTransportProvider> {

    public ConfigurableMcpStdioServerFactory(McpServerConfiguration configuration) {
        super(configuration);
    }

    @Override
    public McpServerTransportProvider transportProvider() {
        return new StdioServerTransportProvider();
    }

    @Override
    public McpAsyncServer create() {
        return McpServer.async(transportProvider())
            .serverInfo(configuration.name(), configuration.version())
            .capabilities(serverCapabilities())
            .instructions(configuration.instructions())
            .requestTimeout(Duration.ofMillis(configuration.requestTimeout()))
            .build();
    }

}
