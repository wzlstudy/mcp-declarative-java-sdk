package com.github.codeboyzhou.mcp.declarative.server.register;

import com.github.codeboyzhou.mcp.declarative.annotation.McpPrompt;
import com.github.codeboyzhou.mcp.declarative.annotation.McpPromptParam;
import com.github.codeboyzhou.mcp.declarative.annotation.McpPrompts;
import com.github.codeboyzhou.mcp.declarative.util.JsonHelper;
import com.github.codeboyzhou.mcp.declarative.util.ReflectionHelper;
import com.github.codeboyzhou.mcp.declarative.util.TypeConverter;
import com.google.inject.Injector;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.spec.McpSchema;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class McpSyncServerPromptRegister extends McpSyncServerComponentRegister<McpServerFeatures.SyncPromptSpecification> {

    private static final Logger logger = LoggerFactory.getLogger(McpSyncServerPromptRegister.class);

    protected McpSyncServerPromptRegister(Injector injector) {
        super(injector);
    }

    @Override
    public void registerTo(McpSyncServer server) {
        Reflections reflections = injector.getInstance(Reflections.class);
        Set<Class<?>> promptClasses = reflections.getTypesAnnotatedWith(McpPrompts.class);
        for (Class<?> promptClass : promptClasses) {
            List<Method> methods = ReflectionHelper.getMethodsAnnotatedWith(promptClass, McpPrompt.class);
            for (Method method : methods) {
                McpServerFeatures.SyncPromptSpecification prompt = createComponentFrom(promptClass, method);
                server.addPrompt(prompt);
            }
        }
    }

    @Override
    public McpServerFeatures.SyncPromptSpecification createComponentFrom(Class<?> clazz, Method method) {
        McpPrompt promptMethod = method.getAnnotation(McpPrompt.class);
        final String name = promptMethod.name().isBlank() ? method.getName() : promptMethod.name();
        final String description = promptMethod.description();
        List<McpSchema.PromptArgument> promptArguments = createPromptArguments(method);
        McpSchema.Prompt prompt = new McpSchema.Prompt(name, description, promptArguments);
        logger.debug("Registering prompt: {}", JsonHelper.toJson(prompt));
        return new McpServerFeatures.SyncPromptSpecification(prompt, (exchange, request) -> {
            Object result;
            try {
                Object instance = injector.getInstance(clazz);
                Map<String, Object> typedParameters = asTypedParameters(method, promptArguments, request.arguments());
                result = method.invoke(instance, typedParameters.values().toArray());
            } catch (Exception e) {
                logger.error("Error invoking prompt method", e);
                result = e + ": " + e.getMessage();
            }
            McpSchema.Content content = new McpSchema.TextContent(result.toString());
            McpSchema.PromptMessage message = new McpSchema.PromptMessage(McpSchema.Role.USER, content);
            return new McpSchema.GetPromptResult(description, List.of(message));
        });
    }

    private List<McpSchema.PromptArgument> createPromptArguments(Method method) {
        List<Parameter> parameters = ReflectionHelper.getParametersAnnotatedWith(method, McpPromptParam.class);
        List<McpSchema.PromptArgument> promptArguments = new ArrayList<>(parameters.size());
        for (Parameter parameter : parameters) {
            McpPromptParam promptParam = parameter.getAnnotation(McpPromptParam.class);
            final String name = promptParam.name();
            final String description = promptParam.description();
            final boolean required = promptParam.required();
            McpSchema.PromptArgument promptArgument = new McpSchema.PromptArgument(name, description, required);
            promptArguments.add(promptArgument);
        }
        return promptArguments;
    }

    private Map<String, Object> asTypedParameters(Method method, List<McpSchema.PromptArgument> arguments, Map<String, Object> parameters) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        Map<String, Object> typedParameters = new LinkedHashMap<>(parameters.size());

        for (int i = 0, size = arguments.size(); i < size; i++) {
            final String parameterName = arguments.get(i).name();
            final Object parameterValue = parameters.get(parameterName);
            // Fill in a default value when the parameter is not specified
            // to ensure that the parameter type is correct when calling method.invoke()
            Object typedParameterValue = TypeConverter.convert(parameterValue, parameterTypes[i]);
            typedParameters.put(parameterName, typedParameterValue);
        }

        return typedParameters;
    }

}
