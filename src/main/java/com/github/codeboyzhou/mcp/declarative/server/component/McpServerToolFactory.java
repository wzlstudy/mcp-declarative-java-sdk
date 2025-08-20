package com.github.codeboyzhou.mcp.declarative.server.component;

import static com.github.codeboyzhou.mcp.declarative.common.InjectorModule.INJECTED_VARIABLE_NAME_I18N_ENABLED;

import com.github.codeboyzhou.mcp.declarative.annotation.McpJsonSchemaDefinition;
import com.github.codeboyzhou.mcp.declarative.annotation.McpJsonSchemaDefinitionProperty;
import com.github.codeboyzhou.mcp.declarative.annotation.McpTool;
import com.github.codeboyzhou.mcp.declarative.annotation.McpToolParam;
import com.github.codeboyzhou.mcp.declarative.common.InjectorProvider;
import com.github.codeboyzhou.mcp.declarative.enums.JsonSchemaDataType;
import com.github.codeboyzhou.mcp.declarative.util.ObjectMappers;
import com.github.codeboyzhou.mcp.declarative.util.Strings;
import com.github.codeboyzhou.mcp.declarative.util.Types;
import com.google.inject.Inject;
import com.google.inject.name.Named;
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
import java.util.stream.Stream;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class McpServerToolFactory
    extends AbstractMcpServerComponentFactory<McpServerFeatures.SyncToolSpecification> {

  private static final Logger log = LoggerFactory.getLogger(McpServerToolFactory.class);

  @Inject
  protected McpServerToolFactory(@Named(INJECTED_VARIABLE_NAME_I18N_ENABLED) Boolean i18nEnabled) {
    super(i18nEnabled);
  }

  @Override
  public McpServerFeatures.SyncToolSpecification create(Class<?> clazz, Method method) {
    McpTool toolMethod = method.getAnnotation(McpTool.class);
    final String name = Strings.defaultIfBlank(toolMethod.name(), method.getName());
    final String title = resolveComponentAttributeValue(toolMethod.title());
    final String description = resolveComponentAttributeValue(toolMethod.description());
    McpSchema.JsonSchema paramSchema = createJsonSchema(method);
    McpSchema.Tool tool =
        McpSchema.Tool.builder()
            .name(name)
            .title(title)
            .description(description)
            .inputSchema(paramSchema)
            .build();
    log.debug("Registering tool: {}", ObjectMappers.toJson(tool));
    return McpServerFeatures.SyncToolSpecification.builder()
        .tool(tool)
        .callHandler(
            (exchange, request) -> {
              Object result;
              boolean isError = false;
              try {
                Object instance = InjectorProvider.getInstance().getInjector().getInstance(clazz);
                Map<String, Object> args = request.arguments();
                Map<String, Object> typedArgs = asTypedParameters(paramSchema, args);
                result = method.invoke(instance, typedArgs.values().toArray());
              } catch (Exception e) {
                log.error("Error invoking tool method", e);
                result = e + ": " + e.getMessage();
                isError = true;
              }
              McpSchema.Content content = new McpSchema.TextContent(result.toString());
              return new McpSchema.CallToolResult(List.of(content), isError);
            })
        .build();
  }

  private McpSchema.JsonSchema createJsonSchema(Method method) {
    Map<String, Object> properties = new LinkedHashMap<>();
    Map<String, Object> definitions = new LinkedHashMap<>();
    List<String> required = new ArrayList<>();

    Stream<Parameter> parameters = Stream.of(method.getParameters());
    List<Parameter> params =
        parameters.filter(p -> p.isAnnotationPresent(McpToolParam.class)).toList();

    for (Parameter param : params) {
      McpToolParam toolParam = param.getAnnotation(McpToolParam.class);
      final String parameterName = toolParam.name();
      Class<?> parameterType = param.getType();
      Map<String, String> property = new HashMap<>();

      if (parameterType.getAnnotation(McpJsonSchemaDefinition.class) == null) {
        property.put("type", parameterType.getSimpleName().toLowerCase());
        property.put("description", resolveComponentAttributeValue(toolParam.description()));
      } else {
        final String parameterTypeSimpleName = parameterType.getSimpleName();
        property.put("$ref", "#/definitions/" + parameterTypeSimpleName);
        Map<String, Object> definition = createJsonSchemaDefinition(parameterType);
        definitions.put(parameterTypeSimpleName, definition);
      }
      properties.put(parameterName, property);

      if (toolParam.required()) {
        required.add(parameterName);
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

    Reflections reflections =
        InjectorProvider.getInstance().getInjector().getInstance(Reflections.class);
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
      fieldProperties.put("type", field.getType().getSimpleName().toLowerCase());
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

  @SuppressWarnings("unchecked")
  private Map<String, Object> asTypedParameters(
      McpSchema.JsonSchema schema, Map<String, Object> parameters) {
    Map<String, Object> properties = schema.properties();
    Map<String, Object> typedParameters = new LinkedHashMap<>(properties.size());

    properties.forEach(
        (parameterName, parameterProperties) -> {
          Object parameterValue = parameters.get(parameterName);
          // Fill in a default value when the parameter is not specified
          // to ensure that the parameter type is correct when calling method.invoke()
          Map<String, Object> map = (Map<String, Object>) parameterProperties;
          final String jsonSchemaType = map.getOrDefault("type", Strings.EMPTY).toString();
          Object typedParameterValue = Types.convert(parameterValue, jsonSchemaType);
          typedParameters.put(parameterName, typedParameterValue);
        });

    return typedParameters;
  }
}
