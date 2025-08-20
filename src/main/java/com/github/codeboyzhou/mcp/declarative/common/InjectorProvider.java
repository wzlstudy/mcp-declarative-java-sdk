package com.github.codeboyzhou.mcp.declarative.common;

import com.google.inject.Injector;

public final class InjectorProvider {

  private static InjectorProvider instance;

  private final Injector injector;

  private InjectorProvider(Injector injector) {
    this.injector = injector;
  }

  public static synchronized InjectorProvider initialize(Injector injector) {
    if (instance == null) {
      instance = new InjectorProvider(injector);
    }
    return instance;
  }

  public static synchronized InjectorProvider getInstance() {
    if (instance == null) {
      throw new IllegalStateException("GuiceInjectorProvider has not been initialized yet.");
    }
    return instance;
  }

  public Injector getInjector() {
    return injector;
  }
}
