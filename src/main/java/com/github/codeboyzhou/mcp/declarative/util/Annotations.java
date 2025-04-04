package com.github.codeboyzhou.mcp.declarative.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

public final class Annotations {

    public static Set<Method> getMethodsAnnotatedWith(Class<?> clazz, Class<? extends Annotation> annotation) {
        Method[] methods = clazz.getMethods();
        return Set.of(methods).stream().filter(m -> m.isAnnotationPresent(annotation)).collect(toSet());
    }

    public static Set<Parameter> getParametersAnnotatedWith(Method method, Class<? extends Annotation> annotation) {
        Parameter[] parameters = method.getParameters();
        return Set.of(parameters).stream().filter(p -> p.isAnnotationPresent(annotation)).collect(toSet());
    }

}
