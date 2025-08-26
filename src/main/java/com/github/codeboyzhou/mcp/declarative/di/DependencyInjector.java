package com.github.codeboyzhou.mcp.declarative.di;

public interface DependencyInjector {

  <T> T getInstance(Class<T> type);

  <T> T getVariable(Class<T> type, String name);

  boolean isInitialized();
}
