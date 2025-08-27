package com.github.codeboyzhou.mcp.declarative.server.component;

import com.github.codeboyzhou.mcp.declarative.annotation.McpPrompt;
import com.github.codeboyzhou.mcp.declarative.annotation.McpResource;
import com.github.codeboyzhou.mcp.declarative.annotation.McpTool;
import com.github.codeboyzhou.mcp.declarative.common.Immutable;
import com.github.codeboyzhou.mcp.declarative.di.DependencyInjector;
import com.github.codeboyzhou.mcp.declarative.di.DependencyInjectorProvider;
import io.modelcontextprotocol.server.McpSyncServer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.function.BiConsumer;
import org.reflections.Reflections;

public final class McpServerComponentRegister {

  private final DependencyInjector injector;

  private final Reflections reflections;

  private final Immutable<McpSyncServer> server;

  public McpServerComponentRegister(McpSyncServer server) {
    this.injector = DependencyInjectorProvider.INSTANCE.getInjector();
    this.reflections = injector.getInstance(Reflections.class);
    this.server = Immutable.of(server);
  }

  public static McpServerComponentRegister of(McpSyncServer server) {
    return new McpServerComponentRegister(server);
  }

  public void registerComponents() {
    register(McpResource.class, McpServerResource.class, McpSyncServer::addResource);
    register(McpPrompt.class, McpServerPrompt.class, McpSyncServer::addPrompt);
    register(McpTool.class, McpServerTool.class, McpSyncServer::addTool);
  }

  private <T> void register(
      Class<? extends Annotation> annotationClass,
      Class<? extends McpServerComponent<T>> componentClass,
      BiConsumer<McpSyncServer, T> serverAddComponent) {

    Set<Method> methods = reflections.getMethodsAnnotatedWith(annotationClass);
    McpServerComponent<T> component = injector.getInstance(componentClass);
    for (Method method : methods) {
      serverAddComponent.accept(server.get(), component.create(method));
    }
  }
}
