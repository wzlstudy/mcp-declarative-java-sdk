package com.github.codeboyzhou.mcp.declarative.enums;

public enum JsonSchemaDataType {

    STRING("string"),
    NUMBER("number"),
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

}
