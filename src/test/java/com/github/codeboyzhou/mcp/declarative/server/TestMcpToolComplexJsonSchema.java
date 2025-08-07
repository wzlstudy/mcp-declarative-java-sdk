package com.github.codeboyzhou.mcp.declarative.server;

import com.github.codeboyzhou.mcp.declarative.annotation.McpJsonSchemaDefinition;
import com.github.codeboyzhou.mcp.declarative.annotation.McpJsonSchemaDefinitionProperty;

@McpJsonSchemaDefinition
public record TestMcpToolComplexJsonSchema(
    @McpJsonSchemaDefinitionProperty(name = "username", description = "username", required = true)
        String name,
    @McpJsonSchemaDefinitionProperty(description = "country") String country,
    String school // for testing property without annotation
    ) {}
