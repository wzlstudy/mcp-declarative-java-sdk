package com.github.codeboyzhou.mcp.declarative.server.component;

import com.github.codeboyzhou.mcp.declarative.annotation.McpPrompt;
import com.github.codeboyzhou.mcp.declarative.annotation.McpPrompts;
import com.github.codeboyzhou.mcp.declarative.annotation.McpResource;
import com.github.codeboyzhou.mcp.declarative.annotation.McpResources;
import com.github.codeboyzhou.mcp.declarative.annotation.McpTool;
import com.github.codeboyzhou.mcp.declarative.annotation.McpTools;
import com.github.codeboyzhou.mcp.declarative.common.Immutable;
import com.github.codeboyzhou.mcp.declarative.common.InjectorProvider;
import com.google.inject.Injector;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.McpSyncServer;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import org.reflections.Reflections;

public final class McpServerComponentRegister {

  private final McpSyncServer server;

  public McpServerComponentRegister(McpSyncServer server) {
    this.server = Immutable.of(server);
  }

  public static McpServerComponentRegister of(McpSyncServer server) {
    return new McpServerComponentRegister(server);
  }

  public void registerComponents() {
    registerResources();
    registerPrompts();
    registerTools();
  }

  private void registerResources() {
    Injector injector = InjectorProvider.getInstance().getInjector();
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
    Injector injector = InjectorProvider.getInstance().getInjector();
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
    Injector injector = InjectorProvider.getInstance().getInjector();
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
