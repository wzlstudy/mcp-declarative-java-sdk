package com.github.codeboyzhou.mcp.declarative.server.component;

import com.github.codeboyzhou.mcp.declarative.annotation.McpJsonSchemaDefinition;
import com.github.codeboyzhou.mcp.declarative.annotation.McpJsonSchemaDefinitionProperty;
import com.github.codeboyzhou.mcp.declarative.annotation.McpTool;
import com.github.codeboyzhou.mcp.declarative.annotation.McpToolParam;
import com.github.codeboyzhou.mcp.declarative.enums.JsonSchemaDataType;
import com.github.codeboyzhou.mcp.declarative.reflect.MethodMetadata;
import com.github.codeboyzhou.mcp.declarative.reflect.ReflectionCache;
import com.github.codeboyzhou.mcp.declarative.server.converter.McpToolParameterConverter;
import com.github.codeboyzhou.mcp.declarative.util.ObjectMappers;
import com.github.codeboyzhou.mcp.declarative.util.Strings;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class McpServerToolFactory
    extends AbstractMcpServerComponentFactory<McpServerFeatures.SyncToolSpecification> {

  private static final Logger log = LoggerFactory.getLogger(McpServerToolFactory.class);

  @Override
  public McpServerFeatures.SyncToolSpecification create(Class<?> clazz, Method method) {
    // Use reflection cache for performance optimization
    MethodMetadata metadata = ReflectionCache.INSTANCE.getMethodMetadata(method);

    McpTool toolMethod = method.getAnnotation(McpTool.class);
    final String name = Strings.defaultIfBlank(toolMethod.name(), method.getName());
    final String title = resolveComponentAttributeValue(toolMethod.title());
    final String description = resolveComponentAttributeValue(toolMethod.description());

    McpSchema.JsonSchema paramSchema = createJsonSchema(metadata);
    McpSchema.Tool tool =
        McpSchema.Tool.builder()
            .name(name)
            .title(title)
            .description(description)
            .inputSchema(paramSchema)
            .build();

    log.debug(
        "Registering tool: {} (Cached: {})",
        ObjectMappers.toJson(tool),
        ReflectionCache.INSTANCE.isCached(method));

    Object instance = injector.getInstance(clazz);
    McpToolParameterConverter converter = injector.getInstance(McpToolParameterConverter.class);

    return McpServerFeatures.SyncToolSpecification.builder()
        .tool(tool)
        .callHandler(
            (exchange, request) -> {
              Object result;
              boolean isError = false;
              try {
                Map<String, Object> arguments = request.arguments();
                List<Object> convertedParams = converter.convertAllParameters(metadata, arguments);
                // Use cached method for invocation
                result = metadata.getMethod().invoke(instance, convertedParams.toArray());
              } catch (Exception e) {
                log.error("Error invoking tool method {}", metadata.getMethodSignature(), e);
                result = e + ": " + e.getMessage();
                isError = true;
              }
              McpSchema.Content content = new McpSchema.TextContent(result.toString());
              return new McpSchema.CallToolResult(List.of(content), isError);
            })
        .build();
  }

  private McpSchema.JsonSchema createJsonSchema(MethodMetadata metadata) {
    Map<String, Object> properties = new LinkedHashMap<>();
    Map<String, Object> definitions = new LinkedHashMap<>();
    List<String> required = new ArrayList<>();

    Parameter[] methodParams = metadata.getParameters();
    for (Parameter param : methodParams) {
      if (param.isAnnotationPresent(McpToolParam.class)) {
        McpToolParam toolParam = param.getAnnotation(McpToolParam.class);
        final String parameterName = toolParam.name();
        Class<?> definitionClass = param.getType();
        Map<String, String> property = new HashMap<>();

        if (definitionClass.isAnnotationPresent(McpJsonSchemaDefinition.class)) {
          final String definitionClassName = definitionClass.getSimpleName();
          property.put("$ref", "#/definitions/" + definitionClassName);
          Map<String, Object> definition = createJsonSchemaDefinition(definitionClass);
          definitions.put(definitionClassName, definition);
        } else {
          property.put("type", definitionClass.getSimpleName().toLowerCase());
          property.put("description", resolveComponentAttributeValue(toolParam.description()));
        }
        properties.put(parameterName, property);

        if (toolParam.required()) {
          required.add(parameterName);
        }
      }
    }

    final boolean hasAdditionalProperties = false;
    return new McpSchema.JsonSchema(
        JsonSchemaDataType.OBJECT.getType(),
        properties,
        required,
        hasAdditionalProperties,
        definitions,
        definitions);
  }

  private Map<String, Object> createJsonSchemaDefinition(Class<?> definitionClass) {
    Map<String, Object> definitionJsonSchema = new HashMap<>();
    definitionJsonSchema.put("type", JsonSchemaDataType.OBJECT.getType());

    Map<String, Object> properties = new LinkedHashMap<>();
    List<String> required = new ArrayList<>();

    Reflections reflections = injector.getInstance(Reflections.class);
    Set<Field> definitionFields =
        reflections.getFieldsAnnotatedWith(McpJsonSchemaDefinitionProperty.class);
    List<Field> fields =
        definitionFields.stream().filter(f -> f.getDeclaringClass() == definitionClass).toList();

    for (Field field : fields) {
      McpJsonSchemaDefinitionProperty property =
          field.getAnnotation(McpJsonSchemaDefinitionProperty.class);
      if (property == null) {
        continue;
      }

      Map<String, Object> fieldProperties = new HashMap<>();
      fieldProperties.put("type", JsonSchemaDataType.fromJavaType(field.getType()).getType());
      fieldProperties.put("description", resolveComponentAttributeValue(property.description()));

      final String fieldName = Strings.defaultIfBlank(property.name(), field.getName());
      properties.put(fieldName, fieldProperties);

      if (property.required()) {
        required.add(fieldName);
      }
    }

    definitionJsonSchema.put("properties", properties);
    definitionJsonSchema.put("required", required);

    return definitionJsonSchema;
  }
}
