package com.github.codeboyzhou.mcp.declarative;

import com.github.codeboyzhou.mcp.declarative.configuration.McpServerConfiguration;
import com.github.codeboyzhou.mcp.declarative.configuration.YAMLConfigurationLoader;
import com.github.codeboyzhou.mcp.declarative.di.DependencyInjector;
import com.github.codeboyzhou.mcp.declarative.di.DependencyInjectorProvider;
import com.github.codeboyzhou.mcp.declarative.di.GuiceDependencyInjector;
import com.github.codeboyzhou.mcp.declarative.di.GuiceInjectorModule;
import com.github.codeboyzhou.mcp.declarative.server.McpServerInfo;
import com.github.codeboyzhou.mcp.declarative.server.factory.McpSseServerFactory;
import com.github.codeboyzhou.mcp.declarative.server.factory.McpSseServerInfo;
import com.github.codeboyzhou.mcp.declarative.server.factory.McpStdioServerFactory;
import com.github.codeboyzhou.mcp.declarative.server.factory.McpStreamableServerFactory;
import com.github.codeboyzhou.mcp.declarative.server.factory.McpStreamableServerInfo;
import com.github.codeboyzhou.mcp.declarative.server.factory.configurable.ConfigurableMcpServerFactories;
import com.google.inject.Guice;
import io.modelcontextprotocol.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class McpServers {

  private static final Logger log = LoggerFactory.getLogger(McpServers.class);

  private static final McpServers INSTANCE = new McpServers();

  private static DependencyInjector injector;

  private McpServers() {
    // Using singleton design pattern should have private constructor
  }

  public static McpServers run(Class<?> applicationMainClass, String[] args) {
    GuiceInjectorModule module = new GuiceInjectorModule(applicationMainClass);
    DependencyInjector injector = new GuiceDependencyInjector(Guice.createInjector(module));
    DependencyInjectorProvider.INSTANCE.initialize(injector);
    McpServers.injector = injector;
    return INSTANCE;
  }

  public void startStdioServer(McpServerInfo serverInfo) {
    injector.getInstance(McpStdioServerFactory.class).startServer(serverInfo);
  }

  public void startSseServer(McpSseServerInfo serverInfo) {
    injector.getInstance(McpSseServerFactory.class).startServer(serverInfo);
  }

  public void startStreamableServer(McpStreamableServerInfo serverInfo) {
    injector.getInstance(McpStreamableServerFactory.class).startServer(serverInfo);
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
    if (configuration.enabled()) {
      ConfigurableMcpServerFactories.getFactory(configuration).startServer();
    } else {
      log.warn("MCP server is disabled, please check your configuration file.");
    }
  }
}
