package com.github.codeboyzhou.mcp.declarative.server;

import com.github.codeboyzhou.mcp.declarative.annotation.McpTool;
import com.github.codeboyzhou.mcp.declarative.annotation.McpToolParam;
import com.github.codeboyzhou.mcp.declarative.annotation.McpTools;

@McpTools
public class TestMcpTools {

    @SuppressWarnings("unused")
    @McpTool(name = "tool1", description = "tool1")
    public static String tool1(
        @McpToolParam(name = "name", description = "name", required = true) String name,
        @McpToolParam(name = "version", description = "version", required = true) String version
    ) {
        return "Hello " + name + ", I am " + version;
    }

    @SuppressWarnings("unused")
    @McpTool(description = "tool2")
    public static String tool2(
        @McpToolParam(name = "name", description = "name") String name,
        @McpToolParam(name = "version", description = "version") String version
    ) {
        return "Hello " + name + ", I am " + version;
    }

    @SuppressWarnings("unused")
    @McpTool(description = "tool3")
    public static String tool3(
        @McpToolParam(name = "complexJsonSchema", description = "complexJsonSchema")
        TestMcpToolComplexJsonSchema complexJsonSchema
    ) {
        return String.format("Hello, my name is %s, I am from %s", complexJsonSchema.name(), complexJsonSchema.country());
    }

}
