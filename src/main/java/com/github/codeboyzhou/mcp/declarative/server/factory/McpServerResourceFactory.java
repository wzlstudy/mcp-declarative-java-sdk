package com.github.codeboyzhou.mcp.declarative.server.factory;

import static com.github.codeboyzhou.mcp.declarative.common.GuiceInjectorModule.INJECTED_VARIABLE_NAME_I18N_ENABLED;

import com.github.codeboyzhou.mcp.declarative.annotation.McpResource;
import com.github.codeboyzhou.mcp.declarative.annotation.McpResources;
import com.github.codeboyzhou.mcp.declarative.common.BufferQueue;
import com.github.codeboyzhou.mcp.declarative.util.ObjectMappers;
import com.github.codeboyzhou.mcp.declarative.util.StringHelper;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;
import io.modelcontextprotocol.server.McpAsyncServer;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

public class McpServerResourceFactory
    extends AbstractMcpServerComponentFactory<McpServerFeatures.AsyncResourceSpecification> {

  private static final Logger logger = LoggerFactory.getLogger(McpServerResourceFactory.class);

  @Inject
  protected McpServerResourceFactory(
      Injector injector, @Named(INJECTED_VARIABLE_NAME_I18N_ENABLED) Boolean i18nEnabled) {
    super(injector, i18nEnabled);
  }

  @Override
  public McpServerFeatures.AsyncResourceSpecification create(Class<?> clazz, Method method) {
    McpResource res = method.getAnnotation(McpResource.class);
    final String name = StringHelper.defaultIfBlank(res.name(), method.getName());
    final String title = resolveComponentAttributeValue(res.title());
    final String description = resolveComponentAttributeValue(res.description());
    McpSchema.Resource resource =
        McpSchema.Resource.builder()
            .uri(res.uri())
            .name(name)
            .title(title)
            .description(description)
            .mimeType(res.mimeType())
            .annotations(new McpSchema.Annotations(List.of(res.roles()), res.priority()))
            .build();
    logger.debug("Registering resource: {}", ObjectMappers.toJson(resource));
    return new McpServerFeatures.AsyncResourceSpecification(
        resource,
        (exchange, request) ->
            Mono.fromSupplier(
                () -> {
                  Object result;
                  try {
                    Object instance = injector.getInstance(clazz);
                    result = method.invoke(instance);
                  } catch (Exception e) {
                    logger.error("Error invoking resource method", e);
                    result = e + ": " + e.getMessage();
                  }
                  McpSchema.ResourceContents contents =
                      new McpSchema.TextResourceContents(
                          resource.uri(), resource.mimeType(), result.toString());
                  return new McpSchema.ReadResourceResult(List.of(contents));
                }));
  }

  @Override
  public void registerTo(McpAsyncServer server) {
    Reflections reflections = injector.getInstance(Reflections.class);
    Set<Class<?>> resourceClasses = reflections.getTypesAnnotatedWith(McpResources.class);
    BufferQueue<McpServerFeatures.AsyncResourceSpecification> queue = new BufferQueue<>();
    for (Class<?> resourceClass : resourceClasses) {
      Set<Method> resourceMethods = reflections.getMethodsAnnotatedWith(McpResource.class);
      List<Method> methods =
          resourceMethods.stream().filter(m -> m.getDeclaringClass() == resourceClass).toList();
      for (Method method : methods) {
        McpServerFeatures.AsyncResourceSpecification resource = create(resourceClass, method);
        queue.submit(resource);
      }
    }
    queue.consume(resource -> server.addResource(resource).subscribe());
  }
}
