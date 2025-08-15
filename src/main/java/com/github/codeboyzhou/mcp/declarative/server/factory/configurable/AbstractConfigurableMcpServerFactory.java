package com.github.codeboyzhou.mcp.declarative.server.factory.configurable;

import com.github.codeboyzhou.mcp.declarative.common.NamedThreadFactory;
import com.github.codeboyzhou.mcp.declarative.configuration.McpServerCapabilities;
import com.github.codeboyzhou.mcp.declarative.configuration.McpServerChangeNotification;
import com.github.codeboyzhou.mcp.declarative.configuration.McpServerConfiguration;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.spec.McpSchema;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class AbstractConfigurableMcpServerFactory implements ConfigurableMcpServerFactory {

  protected final McpServerConfiguration configuration;

  protected final ExecutorService threadPool =
      Executors.newSingleThreadExecutor(new NamedThreadFactory("configurable-mcp-http-server"));

  protected AbstractConfigurableMcpServerFactory(McpServerConfiguration configuration) {
    this.configuration = configuration;
  }

  public McpSyncServer create() {
    return specification()
        .serverInfo(configuration.name(), configuration.version())
        .capabilities(serverCapabilities())
        .instructions(configuration.instructions())
        .requestTimeout(Duration.ofMillis(configuration.requestTimeout()))
        .build();
  }

  private McpSchema.ServerCapabilities serverCapabilities() {
    McpSchema.ServerCapabilities.Builder capabilities = McpSchema.ServerCapabilities.builder();
    McpServerCapabilities capabilitiesConfig = configuration.capabilities();
    McpServerChangeNotification serverChangeNotification = configuration.changeNotification();
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
