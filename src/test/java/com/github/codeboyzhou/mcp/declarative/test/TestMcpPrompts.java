package com.github.codeboyzhou.mcp.declarative.test;

import com.github.codeboyzhou.mcp.declarative.annotation.McpPrompt;
import com.github.codeboyzhou.mcp.declarative.annotation.McpPromptParam;
import com.github.codeboyzhou.mcp.declarative.util.ObjectMappers;
import java.util.Map;

public class TestMcpPrompts {

  @McpPrompt(name = "prompt1_name", title = "prompt1_title", description = "prompt1_description")
  public String prompt1(
      @McpPromptParam(name = "param1", title = "param1_title", description = "param1_description")
          String param1,
      @McpPromptParam(name = "param2", title = "param2_title", description = "param2_description")
          String param2) {
    return ObjectMappers.toJson(Map.of("param1", param1, "param2", param2));
  }
}
