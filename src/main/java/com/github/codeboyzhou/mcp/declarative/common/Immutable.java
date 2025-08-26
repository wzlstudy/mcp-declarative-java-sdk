package com.github.codeboyzhou.mcp.declarative.common;

public record Immutable<T>(T value) {

  public static <T> Immutable<T> of(T value) {
    return new Immutable<>(value);
  }

  public T get() {
    return value;
  }
}
