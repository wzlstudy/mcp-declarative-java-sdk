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
    register(McpResource.class, McpServerResourceFactory.class, McpSyncServer::addResource);
    register(McpPrompt.class, McpServerPromptFactory.class, McpSyncServer::addPrompt);
    register(McpTool.class, McpServerToolFactory.class, McpSyncServer::addTool);
  }

  private <T> void register(
      Class<? extends Annotation> annotationClass,
      Class<? extends McpServerComponentFactory<T>> factoryClass,
      BiConsumer<McpSyncServer, T> serverAddComponent) {

    Set<Method> methods = reflections.getMethodsAnnotatedWith(annotationClass);
    McpServerComponentFactory<T> factory = injector.getInstance(factoryClass);
    for (Method method : methods) {
      T component = factory.create(method.getDeclaringClass(), method);
      serverAddComponent.accept(server.get(), component);
    }
  }
}
