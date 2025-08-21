package com.github.codeboyzhou.mcp.declarative.test;

import com.github.codeboyzhou.mcp.declarative.annotation.McpPrompt;

public class TestMcpPrompts {

  @McpPrompt(name = "prompt1_name", title = "prompt1_title", description = "prompt1_description")
  public String prompt1() {
    return "prompt1_content";
  }
}
