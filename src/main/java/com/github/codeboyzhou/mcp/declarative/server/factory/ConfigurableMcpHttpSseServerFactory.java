package com.github.codeboyzhou.mcp.declarative.server.factory;

import com.github.codeboyzhou.mcp.declarative.configuration.McpServerConfiguration;
import com.github.codeboyzhou.mcp.declarative.configuration.McpServerSSE;
import com.github.codeboyzhou.mcp.declarative.util.JsonHelper;
import io.modelcontextprotocol.server.transport.HttpServletSseServerTransportProvider;

public class ConfigurableMcpHttpSseServerFactory extends AbstractConfigurableMcpServerFactory<HttpServletSseServerTransportProvider> {

    public ConfigurableMcpHttpSseServerFactory(McpServerConfiguration configuration) {
        super(configuration);
    }

    @Override
    public HttpServletSseServerTransportProvider transportProvider() {
        McpServerSSE sse = configuration.sse();
        final String baseUrl = sse.baseUrl();
        final String messageEndpoint = sse.messageEndpoint();
        final String sseEndpoint = sse.endpoint();
        return new HttpServletSseServerTransportProvider(JsonHelper.MAPPER, baseUrl, messageEndpoint, sseEndpoint);
    }

}
