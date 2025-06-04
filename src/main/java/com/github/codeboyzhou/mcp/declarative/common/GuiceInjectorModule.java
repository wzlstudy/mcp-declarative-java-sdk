package com.github.codeboyzhou.mcp.declarative.common;

import com.github.codeboyzhou.mcp.declarative.annotation.McpComponentScan;
import com.github.codeboyzhou.mcp.declarative.annotation.McpPrompts;
import com.github.codeboyzhou.mcp.declarative.annotation.McpResources;
import com.github.codeboyzhou.mcp.declarative.annotation.McpTools;
import com.github.codeboyzhou.mcp.declarative.server.factory.McpServerPromptFactory;
import com.github.codeboyzhou.mcp.declarative.server.factory.McpServerResourceFactory;
import com.github.codeboyzhou.mcp.declarative.server.factory.McpServerToolFactory;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.reflections.Reflections;

import static com.google.inject.Scopes.SINGLETON;
import static org.reflections.scanners.Scanners.FieldsAnnotated;
import static org.reflections.scanners.Scanners.MethodsAnnotated;
import static org.reflections.scanners.Scanners.TypesAnnotated;

public final class GuiceInjectorModule extends AbstractModule {

    private final Class<?> applicationMainClass;

    public GuiceInjectorModule(Class<?> applicationMainClass) {
        this.applicationMainClass = applicationMainClass;
    }

    @Provides
    @Singleton
    @SuppressWarnings("unused")
    public Reflections provideReflections() {
        McpComponentScan scan = applicationMainClass.getAnnotation(McpComponentScan.class);
        final String basePackage = determineBasePackage(scan, applicationMainClass);
        return new Reflections(basePackage, TypesAnnotated, MethodsAnnotated, FieldsAnnotated);
    }

    @Override
    protected void configure() {
        // Bind classes annotated by McpResources, McpPrompts, McpTools
        Reflections reflections = provideReflections();
        reflections.getTypesAnnotatedWith(McpResources.class).forEach(clazz -> bind(clazz).in(SINGLETON));
        reflections.getTypesAnnotatedWith(McpPrompts.class).forEach(clazz -> bind(clazz).in(SINGLETON));
        reflections.getTypesAnnotatedWith(McpTools.class).forEach(clazz -> bind(clazz).in(SINGLETON));
        // Bind all implementations of McpServerComponentFactory
        bind(McpServerResourceFactory.class).in(SINGLETON);
        bind(McpServerPromptFactory.class).in(SINGLETON);
        bind(McpServerToolFactory.class).in(SINGLETON);
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
