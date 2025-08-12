package com.github.codeboyzhou.mcp.declarative;

import com.github.codeboyzhou.mcp.declarative.common.GuiceInjectorModule;
import com.github.codeboyzhou.mcp.declarative.configuration.McpServerConfiguration;
import com.github.codeboyzhou.mcp.declarative.configuration.YAMLConfigurationLoader;
import com.github.codeboyzhou.mcp.declarative.server.component.McpServerPromptFactory;
import com.github.codeboyzhou.mcp.declarative.server.component.McpServerResourceFactory;
import com.github.codeboyzhou.mcp.declarative.server.component.McpServerToolFactory;
import com.github.codeboyzhou.mcp.declarative.server.configurable.ConfigurableMcpHttpSseServerFactory;
import com.github.codeboyzhou.mcp.declarative.server.configurable.ConfigurableMcpServerFactory;
import com.github.codeboyzhou.mcp.declarative.server.configurable.ConfigurableMcpStdioServerFactory;
import com.github.codeboyzhou.mcp.declarative.server.simple.SimpleMcpHttpSseServerFactory;
import com.github.codeboyzhou.mcp.declarative.server.simple.SimpleMcpHttpSseServerInfo;
import com.github.codeboyzhou.mcp.declarative.server.simple.SimpleMcpHttpStreamableServerFactory;
import com.github.codeboyzhou.mcp.declarative.server.simple.SimpleMcpHttpStreamableServerInfo;
import com.github.codeboyzhou.mcp.declarative.server.simple.SimpleMcpServerBaseInfo;
import com.github.codeboyzhou.mcp.declarative.server.simple.SimpleMcpStdioServerFactory;
import com.google.inject.Guice;
import com.google.inject.Injector;
import io.modelcontextprotocol.server.McpAsyncServer;
import io.modelcontextprotocol.spec.McpServerTransportProvider;
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

  public void startStdioServer(SimpleMcpServerBaseInfo serverInfo) {
    SimpleMcpStdioServerFactory factory = new SimpleMcpStdioServerFactory();
    McpAsyncServer server = factory.create(serverInfo);
    registerComponents(server);
  }

  public void startSseServer(SimpleMcpHttpSseServerInfo serverInfo) {
    SimpleMcpHttpSseServerFactory factory = new SimpleMcpHttpSseServerFactory();
    McpAsyncServer server = factory.create(serverInfo);
    registerComponents(server);
  }

  public void startStreamableServer(SimpleMcpHttpStreamableServerInfo serverInfo) {
    SimpleMcpHttpStreamableServerFactory factory = new SimpleMcpHttpStreamableServerFactory();
    McpAsyncServer server = factory.create(serverInfo);
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

    ConfigurableMcpServerFactory<? extends McpServerTransportProvider> factory;
    if (configuration.stdio()) {
      factory = new ConfigurableMcpStdioServerFactory(configuration);
    } else {
      factory = new ConfigurableMcpHttpSseServerFactory(configuration);
    }
    McpAsyncServer server = factory.create();
    registerComponents(server);
  }

  private void registerComponents(McpAsyncServer server) {
    injector.getInstance(McpServerResourceFactory.class).registerTo(server);
    injector.getInstance(McpServerPromptFactory.class).registerTo(server);
    injector.getInstance(McpServerToolFactory.class).registerTo(server);
  }
}
