package com.github.codeboyzhou.mcp.declarative.server.converter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.Map;

public interface ParameterConverter<A extends Annotation> {
  Object convert(Parameter parameter, A annotation, Map<String, Object> args);

  Class<A> getAnnotationType();
}
