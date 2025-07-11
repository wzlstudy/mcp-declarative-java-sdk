package com.github.codeboyzhou.mcp.declarative.annotation;

import com.github.codeboyzhou.mcp.declarative.util.StringHelper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface McpJsonSchemaDefinitionProperty {
    String name() default StringHelper.EMPTY;

    String description() default StringHelper.EMPTY;

    @Deprecated(since = "0.6.0", forRemoval = true)
    String descriptionI18nKey() default StringHelper.EMPTY;

    boolean required() default false;

}
