package com.github.codeboyzhou.mcp.declarative.common;

import static com.google.inject.Scopes.SINGLETON;
import static org.reflections.scanners.Scanners.FieldsAnnotated;
import static org.reflections.scanners.Scanners.MethodsAnnotated;
import static org.reflections.scanners.Scanners.TypesAnnotated;

import com.github.codeboyzhou.mcp.declarative.annotation.McpI18nEnabled;
import com.github.codeboyzhou.mcp.declarative.annotation.McpPrompts;
import com.github.codeboyzhou.mcp.declarative.annotation.McpResources;
import com.github.codeboyzhou.mcp.declarative.annotation.McpServerApplication;
import com.github.codeboyzhou.mcp.declarative.annotation.McpTools;
import com.github.codeboyzhou.mcp.declarative.server.component.McpServerPromptFactory;
import com.github.codeboyzhou.mcp.declarative.server.component.McpServerResourceFactory;
import com.github.codeboyzhou.mcp.declarative.server.component.McpServerToolFactory;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import org.reflections.Reflections;

public final class GuiceInjectorModule extends AbstractModule {

  public static final String INJECTED_VARIABLE_NAME_I18N_ENABLED = "i18nEnabled";

  private final Class<?> mainClass;

  public GuiceInjectorModule(Class<?> mainClass) {
    this.mainClass = mainClass;
  }

  @Provides
  @Singleton
  @SuppressWarnings("unused")
  public Reflections provideReflections() {
    McpServerApplication application = mainClass.getAnnotation(McpServerApplication.class);
    final String basePackage = determineBasePackage(application);
    return new Reflections(basePackage, TypesAnnotated, MethodsAnnotated, FieldsAnnotated);
  }

  @Override
  protected void configure() {
    // Bind classes annotated by McpResources, McpPrompts, McpTools
    Reflections reflections = provideReflections();
    reflections
        .getTypesAnnotatedWith(McpResources.class)
        .forEach(clazz -> bind(clazz).in(SINGLETON));
    reflections.getTypesAnnotatedWith(McpPrompts.class).forEach(clazz -> bind(clazz).in(SINGLETON));
    reflections.getTypesAnnotatedWith(McpTools.class).forEach(clazz -> bind(clazz).in(SINGLETON));

    // Bind all implementations of McpServerComponentFactory
    bind(McpServerResourceFactory.class).in(SINGLETON);
    bind(McpServerPromptFactory.class).in(SINGLETON);
    bind(McpServerToolFactory.class).in(SINGLETON);

    // Bind for boolean variable: i18nEnabled
    final boolean i18nEnabled = mainClass.isAnnotationPresent(McpI18nEnabled.class);
    bind(Boolean.class)
        .annotatedWith(Names.named(INJECTED_VARIABLE_NAME_I18N_ENABLED))
        .toInstance(i18nEnabled);
  }

  private String determineBasePackage(McpServerApplication application) {
    if (application != null) {
      if (!application.basePackage().trim().isBlank()) {
        return application.basePackage();
      }
      if (application.basePackageClass() != Object.class) {
        return application.basePackageClass().getPackageName();
      }
    }
    return mainClass.getPackageName();
  }
}
