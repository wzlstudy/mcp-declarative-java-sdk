package com.github.codeboyzhou.mcp.declarative.test;

import com.github.codeboyzhou.mcp.declarative.annotation.McpResource;
import com.github.codeboyzhou.mcp.declarative.annotation.McpResources;

@McpResources
public class TestMcpResources {

  @McpResource(
      uri = "test://resource1",
      name = "resource1_name",
      title = "resource1_title",
      description = "resource1_description")
  public String resource1() {
    return "resource1_content";
  }
}
