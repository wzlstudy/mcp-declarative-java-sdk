package com.github.codeboyzhou.mcp.declarative;

import com.github.codeboyzhou.mcp.declarative.common.GuiceInjectorModule;
import com.github.codeboyzhou.mcp.declarative.configuration.McpServerConfiguration;
import com.github.codeboyzhou.mcp.declarative.configuration.YAMLConfigurationLoader;
import com.github.codeboyzhou.mcp.declarative.enums.ServerMode;
import com.github.codeboyzhou.mcp.declarative.server.McpServerInfo;
import com.github.codeboyzhou.mcp.declarative.server.component.McpServerPromptFactory;
import com.github.codeboyzhou.mcp.declarative.server.component.McpServerResourceFactory;
import com.github.codeboyzhou.mcp.declarative.server.component.McpServerToolFactory;
import com.github.codeboyzhou.mcp.declarative.server.factory.McpSseServerFactory;
import com.github.codeboyzhou.mcp.declarative.server.factory.McpSseServerInfo;
import com.github.codeboyzhou.mcp.declarative.server.factory.McpStdioServerFactory;
import com.github.codeboyzhou.mcp.declarative.server.factory.McpStreamableServerFactory;
import com.github.codeboyzhou.mcp.declarative.server.factory.McpStreamableServerInfo;
import com.github.codeboyzhou.mcp.declarative.server.factory.configurable.AbstractConfigurableMcpServerFactory;
import com.github.codeboyzhou.mcp.declarative.server.factory.configurable.ConfigurableMcpSseServerFactory;
import com.github.codeboyzhou.mcp.declarative.server.factory.configurable.ConfigurableMcpStdioServerFactory;
import com.github.codeboyzhou.mcp.declarative.server.factory.configurable.ConfigurableMcpStreamableServerFactory;
import com.google.inject.Guice;
import com.google.inject.Injector;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class McpServers {

  private static final Logger logger = LoggerFactory.getLogger(McpServers.class);

  private static final McpServers INSTANCE = new McpServers();

  private static Injector injector;

  private McpServers() {
    // Using singleton design pattern should have private constructor
  }

  public static McpServers run(Class<?> applicationMainClass, String[] args) {
    injector = Guice.createInjector(new GuiceInjectorModule(applicationMainClass));
    return INSTANCE;
  }

  public void startStdioServer(McpServerInfo serverInfo) {
    McpStdioServerFactory factory = new McpStdioServerFactory();
    McpSyncServer server = factory.create(serverInfo);
    registerComponents(server);
  }

  public void startSseServer(McpSseServerInfo serverInfo) {
    McpSseServerFactory factory = new McpSseServerFactory();
    McpSyncServer server = factory.create(serverInfo);
    registerComponents(server);
  }

  public void startStreamableServer(McpStreamableServerInfo serverInfo) {
    McpStreamableServerFactory factory = new McpStreamableServerFactory();
    McpSyncServer server = factory.create(serverInfo);
    registerComponents(server);
  }

  public void startServer(String configFileName) {
    Assert.notNull(configFileName, "configFileName must not be null");
    YAMLConfigurationLoader configLoader = new YAMLConfigurationLoader(configFileName);
    doStartServer(configLoader.loadConfig());
  }

  public void startServer() {
    YAMLConfigurationLoader configLoader = new YAMLConfigurationLoader();
    doStartServer(configLoader.loadConfig());
  }

  private void doStartServer(McpServerConfiguration configuration) {
    if (!configuration.enabled()) {
      logger.warn("MCP server is disabled, please check your configuration file.");
      return;
    }

    AbstractConfigurableMcpServerFactory factory;
    final String mode = configuration.mode().name();

    if (configuration.stdio() || ServerMode.STDIO.name().equalsIgnoreCase(mode)) {
      factory = new ConfigurableMcpStdioServerFactory(configuration);
    } else if (ServerMode.SSE.name().equalsIgnoreCase(mode)) {
      factory = new ConfigurableMcpSseServerFactory(configuration);
    } else if (ServerMode.STREAMABLE.name().equalsIgnoreCase(mode)) {
      factory = new ConfigurableMcpStreamableServerFactory(configuration);
    } else {
      throw new NullPointerException("factory is null, please check your configuration");
    }

    McpSyncServer server = factory.create();
    registerComponents(server);
  }

  private void registerComponents(McpSyncServer server) {
    injector.getInstance(McpServerResourceFactory.class).register(server);
    injector.getInstance(McpServerPromptFactory.class).register(server);
    injector.getInstance(McpServerToolFactory.class).register(server);
  }
}
