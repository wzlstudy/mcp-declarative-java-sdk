package com.github.codeboyzhou.mcp.declarative.util;

import io.modelcontextprotocol.spec.McpSchema;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

public final class ReflectionHelper {

    public static List<Method> getMethodsAnnotatedWith(Class<?> clazz, Class<? extends Annotation> annotation) {
        Method[] methods = clazz.getMethods();
        return Stream.of(methods).filter(m -> m.isAnnotationPresent(annotation)).toList();
    }

    public static List<Parameter> getParametersAnnotatedWith(Method method, Class<? extends Annotation> annotation) {
        Parameter[] parameters = method.getParameters();
        return Stream.of(parameters).filter(p -> p.isAnnotationPresent(annotation)).toList();
    }

    public static void doWithFields(Class<?> clazz, Consumer<Field> consumer) {
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field field : declaredFields) {
            consumer.accept(field);
        }
    }

    public static Object invokeMethod(Class<?> clazz, Method method) throws Exception {
        Object object = clazz.getDeclaredConstructor().newInstance();
        return method.invoke(object);
    }

    public static Object invokeMethod(Class<?> clazz, Method method, List<McpSchema.PromptArgument> arguments, Map<String, Object> parameters) throws Exception {
        Object object = clazz.getDeclaredConstructor().newInstance();
        Map<String, Object> typedParameters = asTypedParameters(method, arguments, parameters);
        return method.invoke(object, typedParameters.values().toArray());
    }

    public static Object invokeMethod(Class<?> clazz, Method method, McpSchema.JsonSchema schema, Map<String, Object> parameters) throws Exception {
        Object object = clazz.getDeclaredConstructor().newInstance();
        Map<String, Object> typedParameters = asTypedParameters(schema, parameters);
        return method.invoke(object, typedParameters.values().toArray());
    }

    private static Map<String, Object> asTypedParameters(Method method, List<McpSchema.PromptArgument> arguments, Map<String, Object> parameters) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        Map<String, Object> typedParameters = new LinkedHashMap<>(parameters.size());

        for (int i = 0, size = arguments.size(); i < size; i++) {
            final String parameterName = arguments.get(i).name();
            final Object parameterValue = parameters.get(parameterName);
            // Fill in a default value when the parameter is not specified
            // to ensure that the parameter type is correct when calling method.invoke()
            Class<?> parameterType = parameterTypes[i];
            if (String.class == parameterType) {
                typedParameters.put(parameterName, parameterValue == null ? StringHelper.EMPTY : parameterValue.toString());
            } else if (int.class == parameterType || Integer.class == parameterType) {
                typedParameters.put(parameterName, parameterValue == null ? 0 : Integer.parseInt(parameterValue.toString()));
            } else if (long.class == parameterType || Long.class == parameterType) {
                typedParameters.put(parameterName, parameterValue == null ? 0 : Long.parseLong(parameterValue.toString()));
            } else if (float.class == parameterType || Float.class == parameterType) {
                typedParameters.put(parameterName, parameterValue == null ? 0.0 : Float.parseFloat(parameterValue.toString()));
            } else if (double.class == parameterType || Double.class == parameterType) {
                typedParameters.put(parameterName, parameterValue == null ? 0.0 : Double.parseDouble(parameterValue.toString()));
            } else if (boolean.class == parameterType || Boolean.class == parameterType) {
                typedParameters.put(parameterName, parameterValue != null && Boolean.parseBoolean(parameterValue.toString()));
            } else {
                typedParameters.put(parameterName, parameterValue);
            }
        }

        return typedParameters;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> asTypedParameters(McpSchema.JsonSchema schema, Map<String, Object> parameters) {
        Map<String, Object> properties = schema.properties();
        Map<String, Object> typedParameters = new LinkedHashMap<>(properties.size());

        properties.forEach((parameterName, parameterProperties) -> {
            Object parameterValue = parameters.get(parameterName);
            // Fill in a default value when the parameter is not specified
            // to ensure that the parameter type is correct when calling method.invoke()
            Map<String, Object> map = (Map<String, Object>) parameterProperties;
            final String jsonSchemaType = map.getOrDefault("type", StringHelper.EMPTY).toString();
            if (String.class.getSimpleName().equalsIgnoreCase(jsonSchemaType)) {
                typedParameters.put(parameterName, parameterValue == null ? StringHelper.EMPTY : parameterValue.toString());
            } else if (Integer.class.getSimpleName().equalsIgnoreCase(jsonSchemaType)) {
                typedParameters.put(parameterName, parameterValue == null ? 0 : Integer.parseInt(parameterValue.toString()));
            } else if (Number.class.getSimpleName().equalsIgnoreCase(jsonSchemaType)) {
                typedParameters.put(parameterName, parameterValue == null ? 0.0 : Double.parseDouble(parameterValue.toString()));
            } else if (Boolean.class.getSimpleName().equalsIgnoreCase(jsonSchemaType)) {
                typedParameters.put(parameterName, parameterValue != null && Boolean.parseBoolean(parameterValue.toString()));
            } else {
                typedParameters.put(parameterName, parameterValue);
            }
        });

        return typedParameters;
    }

}
