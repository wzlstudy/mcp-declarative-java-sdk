package com.github.codeboyzhou.mcp.declarative.server.component;

import com.github.codeboyzhou.mcp.declarative.annotation.McpPrompt;
import com.github.codeboyzhou.mcp.declarative.annotation.McpResource;
import com.github.codeboyzhou.mcp.declarative.annotation.McpTool;
import com.github.codeboyzhou.mcp.declarative.common.Immutable;
import com.github.codeboyzhou.mcp.declarative.common.InjectorProvider;
import com.google.inject.Injector;
import io.modelcontextprotocol.server.McpSyncServer;
import java.lang.reflect.Method;
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
    Set<Method> resourceMethods = reflections.getMethodsAnnotatedWith(McpResource.class);
    for (Method method : resourceMethods) {
      server.addResource(resourceFactory.create(method.getDeclaringClass(), method));
    }
  }

  private void registerPrompts() {
    Injector injector = InjectorProvider.getInstance().getInjector();
    McpServerPromptFactory promptFactory = injector.getInstance(McpServerPromptFactory.class);
    Reflections reflections = injector.getInstance(Reflections.class);
    Set<Method> promptMethods = reflections.getMethodsAnnotatedWith(McpPrompt.class);
    for (Method method : promptMethods) {
      server.addPrompt(promptFactory.create(method.getDeclaringClass(), method));
    }
  }

  private void registerTools() {
    Injector injector = InjectorProvider.getInstance().getInjector();
    McpServerToolFactory toolFactory = injector.getInstance(McpServerToolFactory.class);
    Reflections reflections = injector.getInstance(Reflections.class);
    Set<Method> toolMethods = reflections.getMethodsAnnotatedWith(McpTool.class);
    for (Method method : toolMethods) {
      server.addTool(toolFactory.create(method.getDeclaringClass(), method));
    }
  }
}
