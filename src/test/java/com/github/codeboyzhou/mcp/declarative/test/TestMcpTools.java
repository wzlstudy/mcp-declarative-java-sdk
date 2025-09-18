package com.github.codeboyzhou.mcp.declarative.test;

import com.github.codeboyzhou.mcp.declarative.annotation.McpTool;
import com.github.codeboyzhou.mcp.declarative.annotation.McpToolParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestMcpTools {

  private static final Logger log = LoggerFactory.getLogger(TestMcpTools.class);

  @McpTool(name = "tool1_name", title = "tool1_title", description = "tool1_description")
  public String tool1(
      @McpToolParam(name = "param1", description = "param1_description") String p1,
      @McpToolParam(name = "param2", description = "param2_description", required = true) String p2,
      String p3) {

    log.debug("tool1 called with params: {}, {}, {}", p1, p2, p3);
    return "tool1 is called";
  }

  @McpTool(name = "tool2_name", title = "tool2_title", description = "tool2_description")
  public String tool2(
      @McpToolParam(name = "param1", description = "param1_description") String p1,
      @McpToolParam(name = "param2", description = "param2_description", required = true) String p2,
      String p3) {

    log.debug("tool2 called with params: {}, {}, {}", p1, p2, p3);
    return "tool2 is called";
  }

  @McpTool(name = "tool3_name", title = "tool3_title", description = "tool3_description")
  public void tool3(
      @McpToolParam(name = "param1", description = "param1_description") String p1,
      @McpToolParam(name = "param2", description = "param2_description", required = true) String p2,
      String p3) {

    log.debug("tool3 called with params: {}, {}, {}, and no return value", p1, p2, p3);
  }
}
