package com.github.codeboyzhou.mcp.declarative.util;

import com.github.codeboyzhou.mcp.declarative.annotation.McpComponentScan;
import com.github.codeboyzhou.mcp.declarative.annotation.McpPrompts;
import com.github.codeboyzhou.mcp.declarative.annotation.McpResources;
import com.github.codeboyzhou.mcp.declarative.annotation.McpTools;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

public final class GuiceInjector extends AbstractModule {

    private final Class<?> applicationMainClass;

    public GuiceInjector(Class<?> applicationMainClass) {
        this.applicationMainClass = applicationMainClass;
    }

    @Provides
    @Singleton
    @SuppressWarnings("unused")
    public Reflections provideReflections() {
        McpComponentScan scan = applicationMainClass.getAnnotation(McpComponentScan.class);
        final String basePackage = determineBasePackage(scan, applicationMainClass);
        return new Reflections(basePackage, Scanners.TypesAnnotated, Scanners.MethodsAnnotated, Scanners.FieldsAnnotated);
    }

    @Override
    protected void configure() {
        Reflections reflections = provideReflections();
        reflections.getTypesAnnotatedWith(McpResources.class).forEach(clazz -> bind(clazz).in(Scopes.SINGLETON));
        reflections.getTypesAnnotatedWith(McpPrompts.class).forEach(clazz -> bind(clazz).in(Scopes.SINGLETON));
        reflections.getTypesAnnotatedWith(McpTools.class).forEach(clazz -> bind(clazz).in(Scopes.SINGLETON));
    }

    private String determineBasePackage(McpComponentScan scan, Class<?> applicationMainClass) {
        if (scan != null) {
            if (!scan.basePackage().trim().isBlank()) {
                return scan.basePackage();
            }
            if (scan.basePackageClass() != Object.class) {
                return scan.basePackageClass().getPackageName();
            }
        }
        return applicationMainClass.getPackageName();
    }

}
