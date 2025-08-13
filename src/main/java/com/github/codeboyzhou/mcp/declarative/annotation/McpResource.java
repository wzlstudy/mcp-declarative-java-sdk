package com.github.codeboyzhou.mcp.declarative.annotation;

import com.github.codeboyzhou.mcp.declarative.util.Strings;
import io.modelcontextprotocol.spec.McpSchema;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface McpResource {
  String uri();

  String name() default Strings.EMPTY;

  String title() default Strings.EMPTY;

  String description() default Strings.EMPTY;

  String mimeType() default "text/plain";

  McpSchema.Role[] roles() default {McpSchema.Role.ASSISTANT, McpSchema.Role.USER};

  double priority() default 1.0;
}
