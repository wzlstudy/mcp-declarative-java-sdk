package com.github.codeboyzhou.mcp.declarative.di;

public enum DependencyInjectorProvider {
  INSTANCE;

  private volatile DependencyInjector injector;

  public DependencyInjectorProvider initialize(DependencyInjector injector) {
    if (this.injector == null) {
      synchronized (this) {
        if (this.injector == null) {
          this.injector = injector;
        }
      }
    }
    return this;
  }

  public DependencyInjector getInjector() {
    DependencyInjector current = this.injector;
    if (current == null) {
      throw new IllegalStateException("DependencyInjector has not been initialized yet");
    }
    return current;
  }
}
