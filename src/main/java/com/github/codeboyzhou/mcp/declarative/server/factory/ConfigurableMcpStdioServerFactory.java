package com.github.codeboyzhou.mcp.declarative.server.factory;

import com.github.codeboyzhou.mcp.declarative.configuration.McpServerConfiguration;
import io.modelcontextprotocol.server.transport.StdioServerTransportProvider;
import io.modelcontextprotocol.spec.McpServerTransportProvider;

public class ConfigurableMcpStdioServerFactory extends AbstractConfigurableMcpServerFactory<McpServerTransportProvider> {

    public ConfigurableMcpStdioServerFactory(McpServerConfiguration configuration) {
        super(configuration);
    }

    @Override
    public McpServerTransportProvider transportProvider() {
        return new StdioServerTransportProvider();
    }

}
