package com.github.codeboyzhou.mcp.declarative.util;

import com.github.codeboyzhou.mcp.declarative.annotation.McpComponentScan;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.reflections.Reflections;

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
        return new Reflections(basePackage);
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
