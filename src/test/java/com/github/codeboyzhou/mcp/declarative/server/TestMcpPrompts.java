package com.github.codeboyzhou.mcp.declarative.server;

import com.github.codeboyzhou.mcp.declarative.annotation.McpPrompt;
import com.github.codeboyzhou.mcp.declarative.annotation.McpPromptParam;
import com.github.codeboyzhou.mcp.declarative.annotation.McpPrompts;

@McpPrompts
public class TestMcpPrompts {

    @SuppressWarnings("unused")
    @McpPrompt(name = "prompt1", description = "prompt1")
    public static String prompt1(
        @McpPromptParam(name = "argument1", description = "argument1", required = true) String argument1,
        @McpPromptParam(name = "argument2", description = "argument2", required = true) String argument2
    ) {
        return String.format("This is prompt1, required argument1: %s, required argument2: %s", argument1, argument2);
    }

    @SuppressWarnings("unused")
    @McpPrompt(description = "prompt2")
    public static String prompt2(
        @McpPromptParam(name = "argument1", description = "argument1") String argument1,
        @McpPromptParam(name = "argument2", description = "argument2") String argument2
    ) {
        return String.format("This is prompt2, optional argument1: %s, optional argument2: %s", argument1, argument2);
    }

}
