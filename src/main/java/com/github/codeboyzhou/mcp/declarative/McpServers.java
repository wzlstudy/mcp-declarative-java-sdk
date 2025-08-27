package com.github.codeboyzhou.mcp.declarative;

import com.github.codeboyzhou.mcp.declarative.configuration.McpServerConfiguration;
import com.github.codeboyzhou.mcp.declarative.configuration.YAMLConfigurationLoader;
import com.github.codeboyzhou.mcp.declarative.di.DependencyInjector;
import com.github.codeboyzhou.mcp.declarative.di.DependencyInjectorProvider;
import com.github.codeboyzhou.mcp.declarative.di.GuiceDependencyInjector;
import com.github.codeboyzhou.mcp.declarative.di.GuiceInjectorModule;
import com.github.codeboyzhou.mcp.declarative.server.McpServerInfo;
import com.github.codeboyzhou.mcp.declarative.server.McpSseServer;
import com.github.codeboyzhou.mcp.declarative.server.McpSseServerInfo;
import com.github.codeboyzhou.mcp.declarative.server.McpStdioServer;
import com.github.codeboyzhou.mcp.declarative.server.McpStreamableServer;
import com.github.codeboyzhou.mcp.declarative.server.McpStreamableServerInfo;
import com.github.codeboyzhou.mcp.declarative.server.configurable.ConfigurableMcpServerFactory;
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
    injector.getInstance(McpStdioServer.class).start(serverInfo);
  }

  public void startSseServer(McpSseServerInfo serverInfo) {
    injector.getInstance(McpSseServer.class).start(serverInfo);
  }

  public void startStreamableServer(McpStreamableServerInfo serverInfo) {
    injector.getInstance(McpStreamableServer.class).start(serverInfo);
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
      ConfigurableMcpServerFactory.getServer(configuration).startServer();
    } else {
      log.warn("MCP server is disabled, please check your configuration file.");
    }
  }
}
