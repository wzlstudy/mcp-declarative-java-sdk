package com.github.codeboyzhou.mcp.declarative.enums;

public enum JsonSchemaDataType {
  STRING("string"),
  NUMBER("number"),
  FLOAT("number"),
  DOUBLE("number"),
  INTEGER("integer"),
  BOOLEAN("boolean"),
  OBJECT("object"),
  ;

  private final String type;

  JsonSchemaDataType(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }

  public static JsonSchemaDataType fromJavaType(Class<?> javaType) {
    JsonSchemaDataType[] values = values();
    for (JsonSchemaDataType dataType : values) {
      if (dataType.name().equalsIgnoreCase(javaType.getSimpleName())) {
        return dataType;
      }
    }
    return STRING;
  }
}
