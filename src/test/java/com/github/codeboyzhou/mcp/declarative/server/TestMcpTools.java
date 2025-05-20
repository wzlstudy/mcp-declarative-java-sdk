package com.github.codeboyzhou.mcp.declarative.server;

import com.github.codeboyzhou.mcp.declarative.annotation.McpTool;
import com.github.codeboyzhou.mcp.declarative.annotation.McpToolParam;
import com.github.codeboyzhou.mcp.declarative.annotation.McpTools;

@McpTools
public class TestMcpTools {

    @SuppressWarnings("unused")
    @McpTool(name = "tool1", description = "tool1")
    public static String tool1(
        @McpToolParam(name = "argument1", description = "argument1", required = true) String argument1,
        @McpToolParam(name = "argument2", description = "argument2", required = true) String argument2
    ) {
        return String.format("This is tool1, required argument1: %s, required argument2: %s", argument1, argument2);
    }

    @SuppressWarnings("unused")
    @McpTool(description = "tool2")
    public static String tool2(
        @McpToolParam(name = "argument1", description = "argument1") String argument1,
        @McpToolParam(name = "argument2", description = "argument2") String argument2
    ) {
        return String.format("This is tool2, optional argument1: %s, optional argument2: %s", argument1, argument2);
    }

    @SuppressWarnings("unused")
    @McpTool(description = "tool3")
    public static String tool3(
        @McpToolParam(name = "complexJsonSchema", description = "complexJsonSchema")
        TestMcpToolComplexJsonSchema complexJsonSchema
    ) {
        return String.format("This is tool3 for testing complex json schema: my name is %s, I am from %s",
            complexJsonSchema.name(), complexJsonSchema.country());
    }

}
