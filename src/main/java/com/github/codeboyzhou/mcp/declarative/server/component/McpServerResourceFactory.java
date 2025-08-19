package com.github.codeboyzhou.mcp.declarative.server.component;

import static com.github.codeboyzhou.mcp.declarative.common.GuiceInjectorModule.INJECTED_VARIABLE_NAME_I18N_ENABLED;

import com.github.codeboyzhou.mcp.declarative.annotation.McpResource;
import com.github.codeboyzhou.mcp.declarative.util.ObjectMappers;
import com.github.codeboyzhou.mcp.declarative.util.Strings;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;
import java.lang.reflect.Method;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class McpServerResourceFactory
    extends AbstractMcpServerComponentFactory<McpServerFeatures.SyncResourceSpecification> {

  private static final Logger log = LoggerFactory.getLogger(McpServerResourceFactory.class);

  @Inject
  protected McpServerResourceFactory(
      Injector injector, @Named(INJECTED_VARIABLE_NAME_I18N_ENABLED) Boolean i18nEnabled) {
    super(injector, i18nEnabled);
  }

  @Override
  public McpServerFeatures.SyncResourceSpecification create(Class<?> clazz, Method method) {
    McpResource res = method.getAnnotation(McpResource.class);
    final String name = Strings.defaultIfBlank(res.name(), method.getName());
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
    log.debug("Registering resource: {}", ObjectMappers.toJson(resource));
    return new McpServerFeatures.SyncResourceSpecification(
        resource,
        (exchange, request) -> {
          Object result;
          try {
            Object instance = injector.getInstance(clazz);
            result = method.invoke(instance);
          } catch (Exception e) {
            log.error("Error invoking resource method", e);
            result = e + ": " + e.getMessage();
          }
          McpSchema.ResourceContents contents =
              new McpSchema.TextResourceContents(res.uri(), res.mimeType(), result.toString());
          return new McpSchema.ReadResourceResult(List.of(contents));
        });
  }
}
