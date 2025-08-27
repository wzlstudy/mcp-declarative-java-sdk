package com.github.codeboyzhou.mcp.declarative.server.factory;

import com.github.codeboyzhou.mcp.declarative.configuration.McpServerCapabilities;
import com.github.codeboyzhou.mcp.declarative.configuration.McpServerChangeNotification;
import com.github.codeboyzhou.mcp.declarative.server.McpServerInfo;
import com.github.codeboyzhou.mcp.declarative.server.component.McpServerComponentRegister;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.spec.McpSchema;

public abstract class AbstractMcpServer<S extends McpServerInfo> implements McpServer<S> {

  public void start(S serverInfo) {
    McpSyncServer server =
        sync(serverInfo)
            .serverInfo(serverInfo.name(), serverInfo.version())
            .capabilities(serverCapabilities(serverInfo))
            .instructions(serverInfo.instructions())
            .requestTimeout(serverInfo.requestTimeout())
            .build();
    McpServerComponentRegister.of(server).registerComponents();
  }

  private McpSchema.ServerCapabilities serverCapabilities(S serverInfo) {
    McpSchema.ServerCapabilities.Builder capabilities = McpSchema.ServerCapabilities.builder();
    McpServerCapabilities capabilitiesConfig = serverInfo.capabilities();
    McpServerChangeNotification serverChangeNotification = serverInfo.changeNotification();
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
