package com.github.codeboyzhou.mcp.declarative.test;

import com.github.codeboyzhou.mcp.declarative.annotation.McpTool;

public class TestMcpTools {

  @McpTool(name = "tool1_name", title = "tool1_title", description = "tool1_description")
  public String tool1() {
    return "tool1_content";
  }
}
