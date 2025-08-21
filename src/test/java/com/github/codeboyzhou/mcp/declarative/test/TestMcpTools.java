package com.github.codeboyzhou.mcp.declarative.test;

import com.github.codeboyzhou.mcp.declarative.annotation.McpTool;
import com.github.codeboyzhou.mcp.declarative.annotation.McpToolParam;
import com.github.codeboyzhou.mcp.declarative.util.ObjectMappers;
import java.util.Map;

public class TestMcpTools {

  @McpTool(name = "tool1_name", title = "tool1_title", description = "tool1_description")
  public String tool1(
      @McpToolParam(name = "param1", description = "param1_description") String param1,
      @McpToolParam(name = "param2", description = "param2_description") String param2) {
    return ObjectMappers.toJson(Map.of("param1", param1, "param2", param2));
  }
}
