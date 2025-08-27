package com.github.codeboyzhou.mcp.declarative.server.factory.configurable;

import com.github.codeboyzhou.mcp.declarative.configuration.McpServerCapabilities;
import com.github.codeboyzhou.mcp.declarative.configuration.McpServerChangeNotification;
import com.github.codeboyzhou.mcp.declarative.configuration.McpServerConfiguration;
import com.github.codeboyzhou.mcp.declarative.server.component.McpServerComponentRegister;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.spec.McpSchema;
import java.time.Duration;

public abstract class AbstractConfigurableMcpServer implements ConfigurableMcpServer {

  protected final McpServerConfiguration configuration;

  protected AbstractConfigurableMcpServer(McpServerConfiguration configuration) {
    this.configuration = configuration;
  }

  public void startServer() {
    McpSyncServer server =
        sync()
            .serverInfo(configuration.name(), configuration.version())
            .capabilities(serverCapabilities())
            .instructions(configuration.instructions())
            .requestTimeout(Duration.ofMillis(configuration.requestTimeout()))
            .build();
    McpServerComponentRegister.of(server).registerComponents();
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
