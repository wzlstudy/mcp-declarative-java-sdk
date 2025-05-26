package com.github.codeboyzhou.mcp.declarative.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
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

}
