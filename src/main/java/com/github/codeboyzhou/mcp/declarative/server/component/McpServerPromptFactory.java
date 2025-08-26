package com.github.codeboyzhou.mcp.declarative.server.component;

import com.github.codeboyzhou.mcp.declarative.annotation.McpPrompt;
import com.github.codeboyzhou.mcp.declarative.annotation.McpPromptParam;
import com.github.codeboyzhou.mcp.declarative.reflect.MethodMetadata;
import com.github.codeboyzhou.mcp.declarative.reflect.ReflectionCache;
import com.github.codeboyzhou.mcp.declarative.util.ObjectMappers;
import com.github.codeboyzhou.mcp.declarative.util.Strings;
import com.github.codeboyzhou.mcp.declarative.util.Types;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class McpServerPromptFactory
    extends AbstractMcpServerComponentFactory<McpServerFeatures.SyncPromptSpecification> {

  private static final Logger log = LoggerFactory.getLogger(McpServerPromptFactory.class);

  @Override
  public McpServerFeatures.SyncPromptSpecification create(Class<?> clazz, Method method) {
    // Use reflection cache for performance optimization
    MethodMetadata metadata = ReflectionCache.INSTANCE.getMethodMetadata(method);

    McpPrompt promptMethod = method.getAnnotation(McpPrompt.class);
    final String name = Strings.defaultIfBlank(promptMethod.name(), method.getName());
    final String title = resolveComponentAttributeValue(promptMethod.title());
    final String description = resolveComponentAttributeValue(promptMethod.description());

    List<McpSchema.PromptArgument> promptArguments = createPromptArguments(metadata);
    McpSchema.Prompt prompt = new McpSchema.Prompt(name, title, description, promptArguments);

    log.debug(
        "Registering prompt: {} (Cached: {})",
        ObjectMappers.toJson(prompt),
        ReflectionCache.INSTANCE.isCached(method));

    return new McpServerFeatures.SyncPromptSpecification(
        prompt,
        (exchange, request) -> {
          Object result;
          try {
            Object instance = injector.getInstance(clazz);
            List<Object> typedValues = asTypedParameters(metadata, request.arguments());
            // Use cached method for invocation
            result = metadata.getMethod().invoke(instance, typedValues.toArray());
          } catch (Exception e) {
            log.error("Error invoking prompt method {}", metadata.getMethodSignature(), e);
            result = e + ": " + e.getMessage();
          }
          McpSchema.Content content = new McpSchema.TextContent(result.toString());
          McpSchema.PromptMessage message =
              new McpSchema.PromptMessage(McpSchema.Role.USER, content);
          return new McpSchema.GetPromptResult(description, List.of(message));
        });
  }

  private List<McpSchema.PromptArgument> createPromptArguments(MethodMetadata metadata) {
    Parameter[] methodParams = metadata.getParameters();
    List<McpSchema.PromptArgument> promptArguments = new ArrayList<>(methodParams.length);

    for (Parameter param : methodParams) {
      if (param.isAnnotationPresent(McpPromptParam.class)) {
        McpPromptParam promptParam = param.getAnnotation(McpPromptParam.class);
        final String name = promptParam.name();
        final String title = resolveComponentAttributeValue(promptParam.title());
        final String description = resolveComponentAttributeValue(promptParam.description());
        final boolean required = promptParam.required();
        promptArguments.add(new McpSchema.PromptArgument(name, title, description, required));
      }
    }

    return promptArguments;
  }

  private List<Object> asTypedParameters(MethodMetadata metadata, Map<String, Object> arguments) {
    Parameter[] methodParams = metadata.getParameters();
    List<Object> typedValues = new ArrayList<>(methodParams.length);

    for (Parameter param : methodParams) {
      Object rawValue = null;
      if (param.isAnnotationPresent(McpPromptParam.class)) {
        McpPromptParam promptParam = param.getAnnotation(McpPromptParam.class);
        rawValue = arguments.get(promptParam.name());
      }
      // Fill in a default value when the parameter is not specified or unannotated
      // to ensure that the parameter type is correct when calling method.invoke()
      Class<?> targetType = param.getType();
      Object typedValue = Types.convert(rawValue, targetType);
      typedValues.add(typedValue);
    }

    return typedValues;
  }
}
