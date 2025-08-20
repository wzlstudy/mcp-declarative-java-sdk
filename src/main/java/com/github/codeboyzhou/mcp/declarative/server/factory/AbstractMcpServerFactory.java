package com.github.codeboyzhou.mcp.declarative.server.factory;

import com.github.codeboyzhou.mcp.declarative.common.NamedThreadFactory;
import com.github.codeboyzhou.mcp.declarative.server.McpServerInfo;
import com.github.codeboyzhou.mcp.declarative.server.component.McpServerComponentRegister;
import com.google.inject.Injector;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.spec.McpSchema;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class AbstractMcpServerFactory<S extends McpServerInfo>
    implements McpServerFactory<S> {

  protected final ExecutorService threadPool =
      Executors.newSingleThreadExecutor(new NamedThreadFactory("mcp-http-server"));

  private final Injector injector;

  protected AbstractMcpServerFactory(Injector injector) {
    this.injector = injector;
  }

  public void startServer(S serverInfo) {
    McpSyncServer server =
        sync(serverInfo)
            .serverInfo(serverInfo.name(), serverInfo.version())
            .capabilities(serverCapabilities())
            .instructions(serverInfo.instructions())
            .requestTimeout(serverInfo.requestTimeout())
            .build();
    McpServerComponentRegister.of(injector, server).registerComponents();
  }

  private McpSchema.ServerCapabilities serverCapabilities() {
    return McpSchema.ServerCapabilities.builder()
        .resources(true, true)
        .prompts(true)
        .tools(true)
        .build();
  }
}
