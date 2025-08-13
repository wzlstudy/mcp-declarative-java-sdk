package com.github.codeboyzhou.mcp.declarative;

import com.github.codeboyzhou.mcp.declarative.common.GuiceInjectorModule;
import com.github.codeboyzhou.mcp.declarative.configuration.McpServerConfiguration;
import com.github.codeboyzhou.mcp.declarative.configuration.YAMLConfigurationLoader;
import com.github.codeboyzhou.mcp.declarative.enums.HttpMode;
import com.github.codeboyzhou.mcp.declarative.server.McpHttpServer;
import com.github.codeboyzhou.mcp.declarative.server.component.McpServerPromptFactory;
import com.github.codeboyzhou.mcp.declarative.server.component.McpServerResourceFactory;
import com.github.codeboyzhou.mcp.declarative.server.component.McpServerToolFactory;
import com.github.codeboyzhou.mcp.declarative.server.configurable.ConfigurableMcpHttpSseServerFactory;
import com.github.codeboyzhou.mcp.declarative.server.configurable.ConfigurableMcpHttpStreamableServerFactory;
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
import io.modelcontextprotocol.spec.McpServerTransportProviderBase;
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
    McpAsyncServer server = factory.createServer(serverInfo);
    registerComponentsTo(server);
  }

  public void startSseServer(SimpleMcpHttpSseServerInfo serverInfo) {
    SimpleMcpHttpSseServerFactory factory = new SimpleMcpHttpSseServerFactory();
    McpAsyncServer server = factory.createServer(serverInfo);
    registerComponentsTo(server);
    McpHttpServer httpserver = new McpHttpServer();
    httpserver.use(factory.transportProvider(serverInfo)).bind(serverInfo.port()).start();
  }

  public void startStreamableServer(SimpleMcpHttpStreamableServerInfo serverInfo) {
    SimpleMcpHttpStreamableServerFactory factory = new SimpleMcpHttpStreamableServerFactory();
    McpAsyncServer server = factory.createServer(serverInfo);
    registerComponentsTo(server);
    McpHttpServer httpserver = new McpHttpServer();
    httpserver.use(factory.transportProvider(serverInfo)).bind(serverInfo.port()).start();
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

    ConfigurableMcpServerFactory<? extends McpServerTransportProviderBase> factory;
    if (configuration.stdio()) {
      factory = new ConfigurableMcpStdioServerFactory(configuration);
    } else {
      final String httpMode = configuration.httpMode().name();
      if (HttpMode.SSE.name().equalsIgnoreCase(httpMode)) {
        factory = new ConfigurableMcpHttpSseServerFactory(configuration);
      } else if (HttpMode.STREAMABLE.name().equalsIgnoreCase(httpMode)) {
        factory = new ConfigurableMcpHttpStreamableServerFactory(configuration);
      } else {
        throw new NullPointerException("factory is null, please check your configuration");
      }
    }
    McpAsyncServer server = factory.createServer();
    registerComponentsTo(server);
  }

  private void registerComponentsTo(McpAsyncServer server) {
    McpServerResourceFactory resource = injector.getInstance(McpServerResourceFactory.class);
    McpServerPromptFactory prompt = injector.getInstance(McpServerPromptFactory.class);
    McpServerToolFactory tool = injector.getInstance(McpServerToolFactory.class);
    resource.registerTo(server);
    prompt.registerTo(server);
    tool.registerTo(server);
  }
}
