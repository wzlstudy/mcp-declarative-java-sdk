package com.github.codeboyzhou.mcp.declarative.server.factory.configurable;

import com.github.codeboyzhou.mcp.declarative.common.NamedThreadFactory;
import com.github.codeboyzhou.mcp.declarative.configuration.McpServerCapabilities;
import com.github.codeboyzhou.mcp.declarative.configuration.McpServerChangeNotification;
import com.github.codeboyzhou.mcp.declarative.configuration.McpServerConfiguration;
import com.github.codeboyzhou.mcp.declarative.server.component.McpServerComponentRegister;
import com.google.inject.Injector;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.spec.McpSchema;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class AbstractConfigurableMcpServerFactory implements ConfigurableMcpServerFactory {

  protected final Injector injector;

  protected final McpServerConfiguration config;

  protected final ExecutorService threadPool =
      Executors.newSingleThreadExecutor(new NamedThreadFactory("configurable-mcp-http-server"));

  protected AbstractConfigurableMcpServerFactory(Injector injector, McpServerConfiguration config) {
    this.injector = injector;
    this.config = config;
  }

  public void startServer() {
    McpSyncServer server =
        sync()
            .serverInfo(config.name(), config.version())
            .capabilities(serverCapabilities())
            .instructions(config.instructions())
            .requestTimeout(Duration.ofMillis(config.requestTimeout()))
            .build();
    McpServerComponentRegister.of(injector, server).registerComponents();
  }

  private McpSchema.ServerCapabilities serverCapabilities() {
    McpSchema.ServerCapabilities.Builder capabilities = McpSchema.ServerCapabilities.builder();
    McpServerCapabilities capabilitiesConfig = config.capabilities();
    McpServerChangeNotification serverChangeNotification = config.changeNotification();
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
