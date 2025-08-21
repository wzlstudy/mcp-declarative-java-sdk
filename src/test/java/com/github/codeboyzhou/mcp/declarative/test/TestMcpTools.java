package com.github.codeboyzhou.mcp.declarative.test;

import com.github.codeboyzhou.mcp.declarative.annotation.McpTool;
import com.github.codeboyzhou.mcp.declarative.annotation.McpToolParam;
import com.github.codeboyzhou.mcp.declarative.util.ObjectMappers;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestMcpTools {

  private static final Logger log = LoggerFactory.getLogger(TestMcpTools.class);

  @McpTool(name = "tool1_name", title = "tool1_title", description = "tool1_description")
  public String tool1(
      @McpToolParam(name = "param1", description = "param1_description") String param1,
      @McpToolParam(name = "param2", description = "param2_description", required = true)
          String param2,
      String param3) {
    log.debug("tool1 called with params: {}, {}, {}", param1, param2, param3);
    return ObjectMappers.toJson(Map.of("param1", param1, "param2", param2));
  }
}
