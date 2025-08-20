package com.github.codeboyzhou.mcp.declarative;

import com.github.codeboyzhou.mcp.declarative.common.GuiceInjectorModule;
import com.github.codeboyzhou.mcp.declarative.configuration.McpServerConfiguration;
import com.github.codeboyzhou.mcp.declarative.configuration.YAMLConfigurationLoader;
import com.github.codeboyzhou.mcp.declarative.server.McpServerInfo;
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
import io.modelcontextprotocol.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class McpServers {

  private static final Logger log = LoggerFactory.getLogger(McpServers.class);

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
    McpStdioServerFactory.of(injector).startServer(serverInfo);
  }

  public void startSseServer(McpSseServerInfo serverInfo) {
    McpSseServerFactory.of(injector).startServer(serverInfo);
  }

  public void startStreamableServer(McpStreamableServerInfo serverInfo) {
    McpStreamableServerFactory.of(injector).startServer(serverInfo);
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
      log.warn("MCP server is disabled, please check your configuration file.");
      return;
    }

    AbstractConfigurableMcpServerFactory factory =
        switch (configuration.mode()) {
          case STDIO -> ConfigurableMcpStdioServerFactory.of(injector, configuration);
          case SSE -> ConfigurableMcpSseServerFactory.of(injector, configuration);
          case STREAMABLE -> ConfigurableMcpStreamableServerFactory.of(injector, configuration);
        };

    // Ensure backward compatibility
    if (configuration.stdio()) {
      factory = ConfigurableMcpStdioServerFactory.of(injector, configuration);
    }

    factory.startServer();
  }
}
