package com.github.codeboyzhou.mcp.declarative;

import com.github.codeboyzhou.mcp.declarative.configuration.McpServerConfiguration;
import com.github.codeboyzhou.mcp.declarative.configuration.YAMLConfigurationLoader;
import com.github.codeboyzhou.mcp.declarative.listener.DefaultMcpSyncHttpServerStatusListener;
import com.github.codeboyzhou.mcp.declarative.listener.McpHttpServerStatusListener;
import com.github.codeboyzhou.mcp.declarative.server.McpServerInfo;
import com.github.codeboyzhou.mcp.declarative.server.McpSseServerInfo;
import com.github.codeboyzhou.mcp.declarative.server.factory.ConfigurableMcpSyncServerFactory;
import com.github.codeboyzhou.mcp.declarative.server.factory.McpHttpSseServerFactory;
import com.github.codeboyzhou.mcp.declarative.server.factory.McpStdioServerFactory;
import com.github.codeboyzhou.mcp.declarative.server.register.McpServerComponentRegisters;
import com.github.codeboyzhou.mcp.declarative.util.GuiceInjector;
import com.google.inject.Guice;
import com.google.inject.Injector;
import io.modelcontextprotocol.server.McpAsyncServer;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class McpServers {

    private static final Logger logger = LoggerFactory.getLogger(McpServers.class);

    private static final McpServers INSTANCE = new McpServers();

    private static Injector injector;

    public static McpServers run(Class<?> applicationMainClass, String[] args) {
        injector = Guice.createInjector(new GuiceInjector(applicationMainClass));
        return INSTANCE;
    }

    public void startSyncStdioServer(McpServerInfo serverInfo) {
        McpStdioServerFactory factory = new McpStdioServerFactory();
        McpAsyncServer server = factory.create(serverInfo);
        new McpServerComponentRegisters(injector).registerAllTo(new McpSyncServer(server));
    }

    public void startSyncSseServer(McpSseServerInfo serverInfo, McpHttpServerStatusListener<McpSyncServer> listener) {
        McpHttpSseServerFactory factory = new McpHttpSseServerFactory();
        McpAsyncServer server = factory.create(serverInfo);
        new McpServerComponentRegisters(injector).registerAllTo(new McpSyncServer(server));
    }

    public void startSyncSseServer(McpSseServerInfo serverInfo) {
        startSyncSseServer(serverInfo, new DefaultMcpSyncHttpServerStatusListener());
    }

    public void startServer(String configFileName) {
        Assert.notNull(configFileName, "configFileName must not be null");
        doStartServer(new YAMLConfigurationLoader(configFileName).getConfig());
    }

    public void startServer() {
        doStartServer(new YAMLConfigurationLoader().getConfig());
    }

    private void doStartServer(McpServerConfiguration configuration) {
        if (configuration.enabled()) {
            McpSyncServer server = new ConfigurableMcpSyncServerFactory(configuration).create();
            new McpServerComponentRegisters(injector).registerAllTo(server);
            if (configuration.stdio()) {
                startSyncStdioServer(McpServerInfo.from(configuration));
            } else {
                startSyncSseServer(McpSseServerInfo.from(configuration));
            }
        } else {
            logger.info("MCP server is disabled.");
        }
    }

}
