package com.github.codeboyzhou.mcp.declarative.server.factory;

import com.github.codeboyzhou.mcp.declarative.annotation.McpPrompt;
import com.github.codeboyzhou.mcp.declarative.annotation.McpPrompts;
import com.github.codeboyzhou.mcp.declarative.annotation.McpResource;
import com.github.codeboyzhou.mcp.declarative.annotation.McpResources;
import com.github.codeboyzhou.mcp.declarative.annotation.McpTool;
import com.github.codeboyzhou.mcp.declarative.annotation.McpTools;
import com.github.codeboyzhou.mcp.declarative.common.NamedThreadFactory;
import com.github.codeboyzhou.mcp.declarative.server.McpServerInfo;
import com.github.codeboyzhou.mcp.declarative.server.component.McpServerPromptFactory;
import com.github.codeboyzhou.mcp.declarative.server.component.McpServerResourceFactory;
import com.github.codeboyzhou.mcp.declarative.server.component.McpServerToolFactory;
import com.google.inject.Injector;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.spec.McpSchema;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.reflections.Reflections;

public abstract class AbstractMcpServerFactory<S extends McpServerInfo>
    implements McpServerFactory<S> {

  protected final ExecutorService threadPool =
      Executors.newSingleThreadExecutor(new NamedThreadFactory("mcp-http-server"));

  private final Injector injector;

  private McpSyncServer server;

  protected AbstractMcpServerFactory(Injector injector) {
    this.injector = injector;
  }

  public AbstractMcpServerFactory<S> create(S serverInfo) {
    server =
        sync(serverInfo)
            .serverInfo(serverInfo.name(), serverInfo.version())
            .capabilities(serverCapabilities())
            .instructions(serverInfo.instructions())
            .requestTimeout(serverInfo.requestTimeout())
            .build();
    return this;
  }

  public void start() {
    registerResources();
    registerPrompts();
    registerTools();
  }

  private McpSchema.ServerCapabilities serverCapabilities() {
    return McpSchema.ServerCapabilities.builder()
        .resources(true, true)
        .prompts(true)
        .tools(true)
        .build();
  }

  private void registerResources() {
    McpServerResourceFactory resourceFactory = injector.getInstance(McpServerResourceFactory.class);
    Reflections reflections = injector.getInstance(Reflections.class);
    Set<Class<?>> resourceClasses = reflections.getTypesAnnotatedWith(McpResources.class);
    for (Class<?> resourceClass : resourceClasses) {
      Set<Method> resourceMethods = reflections.getMethodsAnnotatedWith(McpResource.class);
      List<Method> methods =
          resourceMethods.stream().filter(m -> m.getDeclaringClass() == resourceClass).toList();
      for (Method method : methods) {
        McpServerFeatures.SyncResourceSpecification resource =
            resourceFactory.create(resourceClass, method);
        server.addResource(resource);
      }
    }
  }

  private void registerPrompts() {
    McpServerPromptFactory promptFactory = injector.getInstance(McpServerPromptFactory.class);
    Reflections reflections = injector.getInstance(Reflections.class);
    Set<Class<?>> promptClasses = reflections.getTypesAnnotatedWith(McpPrompts.class);
    for (Class<?> promptClass : promptClasses) {
      Set<Method> promptMethods = reflections.getMethodsAnnotatedWith(McpPrompt.class);
      List<Method> methods =
          promptMethods.stream().filter(m -> m.getDeclaringClass() == promptClass).toList();
      for (Method method : methods) {
        McpServerFeatures.SyncPromptSpecification prompt =
            promptFactory.create(promptClass, method);
        server.addPrompt(prompt);
      }
    }
  }

  private void registerTools() {
    McpServerToolFactory toolFactory = injector.getInstance(McpServerToolFactory.class);
    Reflections reflections = injector.getInstance(Reflections.class);
    Set<Class<?>> toolClasses = reflections.getTypesAnnotatedWith(McpTools.class);
    for (Class<?> toolClass : toolClasses) {
      Set<Method> toolMethods = reflections.getMethodsAnnotatedWith(McpTool.class);
      List<Method> methods =
          toolMethods.stream().filter(m -> m.getDeclaringClass() == toolClass).toList();
      for (Method method : methods) {
        McpServerFeatures.SyncToolSpecification tool = toolFactory.create(toolClass, method);
        server.addTool(tool);
      }
    }
  }
}
