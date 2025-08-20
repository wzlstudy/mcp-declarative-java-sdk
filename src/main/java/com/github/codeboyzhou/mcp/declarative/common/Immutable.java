package com.github.codeboyzhou.mcp.declarative.common;

public final class Immutable<T> {
  private final T value;

  private Immutable(T value) {
    this.value = value;
  }

  public static <T> T of(T value) {
    return new Immutable<>(value).value;
  }
}
