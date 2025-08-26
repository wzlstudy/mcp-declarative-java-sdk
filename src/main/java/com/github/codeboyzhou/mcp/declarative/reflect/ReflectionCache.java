package com.github.codeboyzhou.mcp.declarative.reflect;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum ReflectionCache {
  INSTANCE;

  private static final Logger log = LoggerFactory.getLogger(ReflectionCache.class);

  private final ConcurrentHashMap<Method, MethodMetadata> methodCache = new ConcurrentHashMap<>();

  public MethodMetadata getMethodMetadata(Method method) {
    return methodCache.computeIfAbsent(
        method,
        m -> {
          final String className = m.getDeclaringClass().getName();
          log.debug("Caching method metadata for method: {}.{}", className, m.getName());
          return MethodMetadata.of(m);
        });
  }

  public boolean isCached(Method method) {
    return methodCache.containsKey(method);
  }
}
