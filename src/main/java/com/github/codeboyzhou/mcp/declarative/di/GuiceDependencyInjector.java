package com.github.codeboyzhou.mcp.declarative.di;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;

public class GuiceDependencyInjector implements DependencyInjector {

  private final Injector injector;

  public GuiceDependencyInjector(Injector injector) {
    this.injector = injector;
  }

  @Override
  public <T> T getInstance(Class<T> type) {
    if (isInitialized()) {
      return injector.getInstance(type);
    }
    throw new IllegalStateException("GuiceDependencyInjector is not initialized");
  }

  @Override
  public boolean isInitialized() {
    return injector != null;
  }

  @Override
  public <T> T getVariable(Class<T> type, String name) {
    if (isInitialized()) {
      return injector.getInstance(Key.get(type, Names.named(name)));
    }
    throw new IllegalStateException("GuiceDependencyInjector is not initialized");
  }
}
