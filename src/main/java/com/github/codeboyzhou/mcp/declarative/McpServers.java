package com.github.codeboyzhou.mcp.declarative;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.codeboyzhou.mcp.declarative.annotation.McpComponentScan;
import com.github.codeboyzhou.mcp.declarative.configuration.McpServerConfiguration;
import com.github.codeboyzhou.mcp.declarative.configuration.YamlConfigurationLoader;
import com.github.codeboyzhou.mcp.declarative.enums.ServerType;
import com.github.codeboyzhou.mcp.declarative.exception.McpServerException;
import com.github.codeboyzhou.mcp.declarative.listener.DefaultMcpSyncHttpServerStatusListener;
import com.github.codeboyzhou.mcp.declarative.listener.McpHttpServerStatusListener;
import com.github.codeboyzhou.mcp.declarative.server.McpHttpServer;
import com.github.codeboyzhou.mcp.declarative.server.McpServerComponentRegisters;
import com.github.codeboyzhou.mcp.declarative.server.McpServerFactory;
import com.github.codeboyzhou.mcp.declarative.server.McpServerInfo;
import com.github.codeboyzhou.mcp.declarative.server.McpSseServerInfo;
import com.github.codeboyzhou.mcp.declarative.server.McpSyncServerFactory;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.transport.HttpServletSseServerTransportProvider;
import io.modelcontextprotocol.server.transport.StdioServerTransportProvider;
import io.modelcontextprotocol.spec.McpServerTransportProvider;
import io.modelcontextprotocol.util.Assert;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;

public class McpServers {

    private static final Logger logger = LoggerFactory.getLogger(McpServers.class);

    private static final McpServers INSTANCE = new McpServers();

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static Reflections reflections;

    public static McpServers run(Class<?> applicationMainClass, String[] args) {
        McpComponentScan scan = applicationMainClass.getAnnotation(McpComponentScan.class);
        reflections = new Reflections(determineBasePackage(scan, applicationMainClass));
        return INSTANCE;
    }

    private static String determineBasePackage(McpComponentScan scan, Class<?> applicationMainClass) {
        if (scan != null) {
            if (!scan.basePackage().trim().isBlank()) {
                return scan.basePackage();
            }
            if (scan.basePackageClass() != Object.class) {
                return scan.basePackageClass().getPackageName();
            }
        }
        return applicationMainClass.getPackageName();
    }

    @Deprecated(since = "0.4.0")
    public void startSyncStdioServer(String name, String version, String instructions) {
        McpServerInfo serverInfo = McpServerInfo.builder().name(name).version(version)
            .instructions(instructions).requestTimeout(Duration.ofSeconds(10)).build();
        startSyncStdioServer(serverInfo);
    }

    public void startSyncStdioServer(McpServerInfo serverInfo) {
        McpServerFactory<McpSyncServer> factory = new McpSyncServerFactory();
        McpServerTransportProvider transportProvider = new StdioServerTransportProvider();
        McpSyncServer server = factory.create(serverInfo, transportProvider);
        McpServerComponentRegisters.registerAllTo(server, reflections);
    }

    public void startSyncSseServer(McpSseServerInfo serverInfo, McpHttpServerStatusListener<McpSyncServer> listener) {
        McpServerFactory<McpSyncServer> factory = new McpSyncServerFactory();
        HttpServletSseServerTransportProvider transportProvider = new HttpServletSseServerTransportProvider(
            OBJECT_MAPPER, serverInfo.baseUrl(), serverInfo.messageEndpoint(), serverInfo.sseEndpoint()
        );
        McpSyncServer server = factory.create(serverInfo, transportProvider);
        McpServerComponentRegisters.registerAllTo(server, reflections);
        McpHttpServer<McpSyncServer> httpServer = new McpHttpServer<>();
        httpServer.with(transportProvider).with(serverInfo).with(listener).attach(server).start();
    }

    public void startSyncSseServer(McpSseServerInfo serverInfo) {
        startSyncSseServer(serverInfo, new DefaultMcpSyncHttpServerStatusListener());
    }

    public void startServer(String configFileName) {
        Assert.notNull(configFileName, "configFileName must not be null");
        YamlConfigurationLoader configurationLoader = new YamlConfigurationLoader();
        McpServerConfiguration configuration;
        try {
            configuration = configurationLoader.load(configFileName);
            if (configuration.enabled()) {
                startServerWith(configuration);
            } else {
                logger.info("MCP server is disabled.");
            }
        } catch (IOException e) {
            throw new McpServerException("Error loading configuration file: " + e.getMessage(), e);
        }
    }

    public void startServer() {
        YamlConfigurationLoader configurationLoader = new YamlConfigurationLoader();
        McpServerConfiguration configuration = configurationLoader.loadConfiguration();
        startServerWith(configuration);
    }

    private void startServerWith(McpServerConfiguration configuration) {
        if (ServerType.SYNC.name().equalsIgnoreCase(configuration.type())) {
            if (configuration.stdio()) {
                McpServerInfo serverInfo = McpServerInfo.builder()
                    .name(configuration.name())
                    .version(configuration.version())
                    .instructions(configuration.instructions())
                    .requestTimeout(Duration.ofSeconds(configuration.requestTimeout()))
                    .build();
                startSyncStdioServer(serverInfo);
            } else {
                McpSseServerInfo serverInfo = McpSseServerInfo.builder()
                    .name(configuration.name())
                    .version(configuration.version())
                    .instructions(configuration.instructions())
                    .requestTimeout(Duration.ofSeconds(configuration.requestTimeout()))
                    .baseUrl(configuration.baseUrl())
                    .messageEndpoint(configuration.sseMessageEndpoint())
                    .sseEndpoint(configuration.sseEndpoint())
                    .port(configuration.ssePort())
                    .build();
                startSyncSseServer(serverInfo);
            }
        }
    }

}
