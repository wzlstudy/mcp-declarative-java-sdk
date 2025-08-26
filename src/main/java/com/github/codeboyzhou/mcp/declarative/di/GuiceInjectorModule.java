package com.github.codeboyzhou.mcp.declarative.di;

import static com.google.inject.Scopes.SINGLETON;
import static java.util.stream.Collectors.toSet;
import static org.reflections.scanners.Scanners.FieldsAnnotated;
import static org.reflections.scanners.Scanners.MethodsAnnotated;

import com.github.codeboyzhou.mcp.declarative.annotation.McpI18nEnabled;
import com.github.codeboyzhou.mcp.declarative.annotation.McpPrompt;
import com.github.codeboyzhou.mcp.declarative.annotation.McpResource;
import com.github.codeboyzhou.mcp.declarative.annotation.McpServerApplication;
import com.github.codeboyzhou.mcp.declarative.annotation.McpTool;
import com.github.codeboyzhou.mcp.declarative.server.component.McpServerPromptFactory;
import com.github.codeboyzhou.mcp.declarative.server.component.McpServerResourceFactory;
import com.github.codeboyzhou.mcp.declarative.server.component.McpServerToolFactory;
import com.github.codeboyzhou.mcp.declarative.server.factory.McpSseServerFactory;
import com.github.codeboyzhou.mcp.declarative.server.factory.McpStdioServerFactory;
import com.github.codeboyzhou.mcp.declarative.server.factory.McpStreamableServerFactory;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;
import org.reflections.Reflections;

public final class GuiceInjectorModule extends AbstractModule {

  public static final String INJECTED_VARIABLE_NAME_I18N_ENABLED = "i18nEnabled";

  private final Class<?> mainClass;

  public GuiceInjectorModule(Class<?> mainClass) {
    this.mainClass = mainClass;
  }

  @Override
  protected void configure() {
    // Bind classes annotated by McpResources, McpPrompts, McpTools
    bindClassesOfMethodsAnnotatedWith(McpResource.class);
    bindClassesOfMethodsAnnotatedWith(McpPrompt.class);
    bindClassesOfMethodsAnnotatedWith(McpTool.class);

    // Bind all implementations of McpServerComponentFactory
    bind(McpServerResourceFactory.class).in(SINGLETON);
    bind(McpServerPromptFactory.class).in(SINGLETON);
    bind(McpServerToolFactory.class).in(SINGLETON);

    // Bind all implementations of McpServerFactory
    bind(McpStdioServerFactory.class).in(SINGLETON);
    bind(McpSseServerFactory.class).in(SINGLETON);
    bind(McpStreamableServerFactory.class).in(SINGLETON);

    // Bind for boolean variable: i18nEnabled
    final boolean i18nEnabled = mainClass.isAnnotationPresent(McpI18nEnabled.class);
    bind(Boolean.class)
        .annotatedWith(Names.named(INJECTED_VARIABLE_NAME_I18N_ENABLED))
        .toInstance(i18nEnabled);
  }

  @Provides
  @Singleton
  public Reflections provideReflections() {
    McpServerApplication application = mainClass.getAnnotation(McpServerApplication.class);
    final String basePackage = determineBasePackage(application);
    return new Reflections(basePackage, MethodsAnnotated, FieldsAnnotated);
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

  private void bindClassesOfMethodsAnnotatedWith(Class<? extends Annotation> annotation) {
    Reflections reflections = provideReflections();
    Set<Method> methods = reflections.getMethodsAnnotatedWith(annotation);
    Set<Class<?>> classes = methods.stream().map(Method::getDeclaringClass).collect(toSet());
    classes.forEach(clazz -> bind(clazz).in(SINGLETON));
  }
}
