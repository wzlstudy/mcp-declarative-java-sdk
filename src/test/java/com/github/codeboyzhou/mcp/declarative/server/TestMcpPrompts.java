package com.github.codeboyzhou.mcp.declarative.server;

import com.github.codeboyzhou.mcp.declarative.annotation.McpPrompt;
import com.github.codeboyzhou.mcp.declarative.annotation.McpPromptParam;
import com.github.codeboyzhou.mcp.declarative.annotation.McpPrompts;

@McpPrompts
public class TestMcpPrompts {

    @McpPrompt(name = "prompt1", description = "prompt1")
    public static String prompt1(
        @McpPromptParam(name = "name", description = "name", required = true) String name,
        @McpPromptParam(name = "version", description = "version", required = true) String version
    ) {
        return "Hello " + name + ", I am " + version;
    }

    @McpPrompt(description = "prompt2")
    public static String prompt2(
        @McpPromptParam(name = "name", description = "name") String name,
        @McpPromptParam(name = "version", description = "version") String version
    ) {
        return "Hello " + name + ", I am " + version;
    }

}
