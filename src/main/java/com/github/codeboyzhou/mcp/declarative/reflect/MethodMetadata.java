package com.github.codeboyzhou.mcp.declarative.reflect;

import com.github.codeboyzhou.mcp.declarative.common.Immutable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Objects;

public final class MethodMetadata {

  private final Immutable<Method> method;

  private final Parameter[] parameters;

  private final String methodSignature;

  public MethodMetadata(Method method) {
    this.method = Immutable.of(method);
    this.parameters = method.getParameters();
    this.methodSignature = method.toGenericString();
  }

  public static MethodMetadata of(Method method) {
    return new MethodMetadata(method);
  }

  public Method getMethod() {
    return method.get();
  }

  public Parameter[] getParameters() {
    return parameters.clone();
  }

  public String getMethodSignature() {
    return methodSignature;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    MethodMetadata that = (MethodMetadata) obj;
    return Objects.equals(method, that.method);
  }

  @Override
  public int hashCode() {
    return Objects.hash(method);
  }

  @Override
  public String toString() {
    return String.format("MethodMetadata{methodSignature=%s}", methodSignature);
  }
}
