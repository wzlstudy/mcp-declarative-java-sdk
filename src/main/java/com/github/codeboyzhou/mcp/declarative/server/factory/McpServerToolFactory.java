package com.github.codeboyzhou.mcp.declarative.server.factory;

import com.github.codeboyzhou.mcp.declarative.annotation.McpJsonSchemaDefinition;
import com.github.codeboyzhou.mcp.declarative.annotation.McpJsonSchemaDefinitionProperty;
import com.github.codeboyzhou.mcp.declarative.annotation.McpTool;
import com.github.codeboyzhou.mcp.declarative.annotation.McpToolParam;
import com.github.codeboyzhou.mcp.declarative.annotation.McpTools;
import com.github.codeboyzhou.mcp.declarative.common.BufferQueue;
import com.github.codeboyzhou.mcp.declarative.enums.JsonSchemaDataType;
import com.github.codeboyzhou.mcp.declarative.util.JsonHelper;
import com.github.codeboyzhou.mcp.declarative.util.StringHelper;
import com.github.codeboyzhou.mcp.declarative.util.TypeConverter;
import com.google.inject.Inject;
import com.google.inject.Injector;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.spec.McpSchema;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

public class McpServerToolFactory extends AbstractMcpServerComponentFactory<McpServerFeatures.SyncToolSpecification> {

    private static final Logger logger = LoggerFactory.getLogger(McpServerToolFactory.class);

    @Inject
    protected McpServerToolFactory(Injector injector) {
        super(injector);
    }

    @Override
    public McpServerFeatures.SyncToolSpecification create(Class<?> clazz, Method method) {
        McpTool toolMethod = method.getAnnotation(McpTool.class);
        McpSchema.JsonSchema paramSchema = createJsonSchema(method);
        final String name = toolMethod.name().isBlank() ? method.getName() : toolMethod.name();
        McpSchema.Tool tool = new McpSchema.Tool(name, toolMethod.description(), paramSchema);
        logger.debug("Registering tool: {}", JsonHelper.toJson(tool));
        return new McpServerFeatures.SyncToolSpecification(tool, (exchange, params) -> {
            Object result;
            boolean isError = false;
            try {
                Object instance = injector.getInstance(clazz);
                Map<String, Object> typedParameters = asTypedParameters(paramSchema, params);
                result = method.invoke(instance, typedParameters.values().toArray());
            } catch (Exception e) {
                logger.error("Error invoking tool method", e);
                result = e + ": " + e.getMessage();
                isError = true;
            }
            McpSchema.Content content = new McpSchema.TextContent(result.toString());
            return new McpSchema.CallToolResult(List.of(content), isError);
        });
    }

    @Override
    public void registerTo(McpSyncServer server) {
        Reflections reflections = injector.getInstance(Reflections.class);
        Set<Class<?>> toolClasses = reflections.getTypesAnnotatedWith(McpTools.class);
        BufferQueue<McpServerFeatures.SyncToolSpecification> queue = new BufferQueue<>();
        for (Class<?> toolClass : toolClasses) {
            Set<Method> toolMethods = reflections.getMethodsAnnotatedWith(McpTool.class);
            List<Method> methods = toolMethods.stream().filter(m -> m.getDeclaringClass() == toolClass).toList();
            for (Method method : methods) {
                McpServerFeatures.SyncToolSpecification tool = create(toolClass, method);
                queue.submit(tool);
            }
        }
        queue.consume(server::addTool);
    }

    private McpSchema.JsonSchema createJsonSchema(Method method) {
        Map<String, Object> properties = new LinkedHashMap<>();
        Map<String, Object> definitions = new LinkedHashMap<>();
        List<String> required = new ArrayList<>();

        Stream<Parameter> parameters = Stream.of(method.getParameters());
        List<Parameter> params = parameters.filter(p -> p.isAnnotationPresent(McpToolParam.class)).toList();

        for (Parameter param : params) {
            McpToolParam toolParam = param.getAnnotation(McpToolParam.class);
            final String parameterName = toolParam.name();
            Class<?> parameterType = param.getType();
            Map<String, String> property = new HashMap<>();

            if (parameterType.getAnnotation(McpJsonSchemaDefinition.class) == null) {
                property.put("type", parameterType.getSimpleName().toLowerCase());
                property.put("description", toolParam.description());
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
        return new McpSchema.JsonSchema(JsonSchemaDataType.OBJECT.getType(), properties, required, hasAdditionalProperties, definitions, definitions);
    }

    private Map<String, Object> createJsonSchemaDefinition(Class<?> definitionClass) {
        Map<String, Object> definitionJsonSchema = new HashMap<>();
        definitionJsonSchema.put("type", JsonSchemaDataType.OBJECT.getType());

        Map<String, Object> properties = new LinkedHashMap<>();
        List<String> required = new ArrayList<>();

        Reflections reflections = injector.getInstance(Reflections.class);
        Set<Field> definitionFields = reflections.getFieldsAnnotatedWith(McpJsonSchemaDefinitionProperty.class);
        List<Field> fields = definitionFields.stream().filter(f -> f.getDeclaringClass() == definitionClass).toList();

        for (Field field : fields) {
            McpJsonSchemaDefinitionProperty property = field.getAnnotation(McpJsonSchemaDefinitionProperty.class);
            if (property == null) {
                continue;
            }

            Map<String, Object> fieldProperties = new HashMap<>();
            fieldProperties.put("type", field.getType().getSimpleName().toLowerCase());
            fieldProperties.put("description", property.description());

            final String fieldName = property.name().isBlank() ? field.getName() : property.name();
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
    private Map<String, Object> asTypedParameters(McpSchema.JsonSchema schema, Map<String, Object> parameters) {
        Map<String, Object> properties = schema.properties();
        Map<String, Object> typedParameters = new LinkedHashMap<>(properties.size());

        properties.forEach((parameterName, parameterProperties) -> {
            Object parameterValue = parameters.get(parameterName);
            // Fill in a default value when the parameter is not specified
            // to ensure that the parameter type is correct when calling method.invoke()
            Map<String, Object> map = (Map<String, Object>) parameterProperties;
            final String jsonSchemaType = map.getOrDefault("type", StringHelper.EMPTY).toString();
            Object typedParameterValue = TypeConverter.convert(parameterValue, jsonSchemaType);
            typedParameters.put(parameterName, typedParameterValue);
        });

        return typedParameters;
    }

}
