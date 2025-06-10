package com.github.codeboyzhou.mcp.declarative.server.factory;

import com.github.codeboyzhou.mcp.declarative.configuration.McpServerCapabilities;
import com.github.codeboyzhou.mcp.declarative.configuration.McpServerChangeNotification;
import com.github.codeboyzhou.mcp.declarative.configuration.McpServerConfiguration;
import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpServerTransportProvider;

public abstract class AbstractConfigurableMcpServerFactory<T extends McpServerTransportProvider> implements ConfigurableMcpServerFactory<T> {

    protected final McpServerConfiguration configuration;

    protected AbstractConfigurableMcpServerFactory(McpServerConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public McpSchema.ServerCapabilities serverCapabilities() {
        McpSchema.ServerCapabilities.Builder capabilities = McpSchema.ServerCapabilities.builder();
        McpServerCapabilities capabilitiesConfig = configuration.capabilities();
        McpServerChangeNotification serverChangeNotification = configuration.changeNotification();
        if (capabilitiesConfig.resource()) {
            capabilities.resources(true, serverChangeNotification.resource());
        }
        if (capabilitiesConfig.prompt()) {
            capabilities.prompts(serverChangeNotification.prompt());
        }
        if (capabilitiesConfig.tool()) {
            capabilities.tools(serverChangeNotification.tool());
        }
        return capabilities.build();
    }

}
