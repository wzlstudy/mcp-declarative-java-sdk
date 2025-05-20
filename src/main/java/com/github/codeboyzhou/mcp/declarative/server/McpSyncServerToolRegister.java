package com.github.codeboyzhou.mcp.declarative.server;

import com.github.codeboyzhou.mcp.declarative.annotation.McpJsonSchemaDefinition;
import com.github.codeboyzhou.mcp.declarative.annotation.McpJsonSchemaDefinitionProperty;
import com.github.codeboyzhou.mcp.declarative.annotation.McpTool;
import com.github.codeboyzhou.mcp.declarative.annotation.McpToolParam;
import com.github.codeboyzhou.mcp.declarative.util.JsonHelper;
import com.github.codeboyzhou.mcp.declarative.util.ReflectionHelper;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.spec.McpSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class McpSyncServerToolRegister
    implements McpServerComponentRegister<McpSyncServer, McpServerFeatures.SyncToolSpecification> {

    private static final Logger logger = LoggerFactory.getLogger(McpSyncServerToolRegister.class);

    private static final String OBJECT_TYPE_NAME = Object.class.getSimpleName().toLowerCase();

    private final Set<Class<?>> toolClasses;

    public McpSyncServerToolRegister(Set<Class<?>> toolClasses) {
        this.toolClasses = toolClasses;
    }

    @Override
    public void registerTo(McpSyncServer server) {
        for (Class<?> toolClass : toolClasses) {
            List<Method> methods = ReflectionHelper.getMethodsAnnotatedWith(toolClass, McpTool.class);
            for (Method method : methods) {
                McpServerFeatures.SyncToolSpecification tool = createComponentFrom(toolClass, method);
                server.addTool(tool);
            }
        }
    }

    @Override
    public McpServerFeatures.SyncToolSpecification createComponentFrom(Class<?> clazz, Method method) {
        McpTool toolMethod = method.getAnnotation(McpTool.class);
        McpSchema.JsonSchema paramSchema = createJsonSchema(method);
        final String name = toolMethod.name().isBlank() ? method.getName() : toolMethod.name();
        McpSchema.Tool tool = new McpSchema.Tool(name, toolMethod.description(), paramSchema);
        logger.debug("Registering tool: {}", JsonHelper.toJson(tool));
        return new McpServerFeatures.SyncToolSpecification(tool, (exchange, params) -> {
            Object result;
            boolean isError = false;
            try {
                result = ReflectionHelper.invokeMethod(clazz, method, paramSchema, params);
            } catch (Throwable e) {
                logger.error("Error invoking tool method", e);
                result = e + ": " + e.getMessage();
                isError = true;
            }
            McpSchema.Content content = new McpSchema.TextContent(result.toString());
            return new McpSchema.CallToolResult(List.of(content), isError);
        });
    }

    private McpSchema.JsonSchema createJsonSchema(Method method) {
        Map<String, Object> properties = new LinkedHashMap<>();
        Map<String, Object> definitions = new LinkedHashMap<>();
        List<String> required = new ArrayList<>();

        List<Parameter> parameters = ReflectionHelper.getParametersAnnotatedWith(method, McpToolParam.class);
        for (Parameter parameter : parameters) {
            McpToolParam toolParam = parameter.getAnnotation(McpToolParam.class);
            final String parameterName = toolParam.name();
            Class<?> parameterType = parameter.getType();
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
        return new McpSchema.JsonSchema(OBJECT_TYPE_NAME, properties, required, hasAdditionalProperties, definitions, definitions);
    }

    private Map<String, Object> createJsonSchemaDefinition(Class<?> definitionClass) {
        Map<String, Object> definitionJsonSchema = new HashMap<>();
        definitionJsonSchema.put("type", OBJECT_TYPE_NAME);

        Map<String, Object> properties = new LinkedHashMap<>();
        List<String> required = new ArrayList<>();

        ReflectionHelper.doWithFields(definitionClass, field -> {
            McpJsonSchemaDefinitionProperty property = field.getAnnotation(McpJsonSchemaDefinitionProperty.class);
            if (property == null) {
                return;
            }

            Map<String, Object> fieldProperties = new HashMap<>();
            fieldProperties.put("type", field.getType().getSimpleName().toLowerCase());
            fieldProperties.put("description", property.description());

            final String fieldName = property.name().isBlank() ? field.getName() : property.name();
            properties.put(fieldName, fieldProperties);

            if (property.required()) {
                required.add(fieldName);
            }
        });

        definitionJsonSchema.put("properties", properties);
        definitionJsonSchema.put("required", required);

        return definitionJsonSchema;
    }

}
