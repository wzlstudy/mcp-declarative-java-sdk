package com.github.codeboyzhou.mcp.declarative.test;

import com.github.codeboyzhou.mcp.declarative.annotation.McpPrompt;
import com.github.codeboyzhou.mcp.declarative.annotation.McpPromptParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestMcpPrompts {

  private static final Logger log = LoggerFactory.getLogger(TestMcpPrompts.class);

  @McpPrompt(name = "prompt1_name", title = "prompt1_title", description = "prompt1_description")
  public String prompt1(
      @McpPromptParam(name = "param1", title = "param1_title", description = "param1_description")
          String param1,
      @McpPromptParam(name = "param2", title = "param2_title", description = "param2_description")
          String param2,
      String param3) {

    log.debug("prompt1 called with params: {}, {}, {}", param1, param2, param3);
    return "prompt1 is called";
  }

  @McpPrompt(name = "prompt2_name", title = "prompt2_title", description = "prompt2_description")
  public String prompt2(
      @McpPromptParam(name = "param1", title = "param1_title", description = "param1_description")
          String param1,
      @McpPromptParam(name = "param2", title = "param2_title", description = "param2_description")
          String param2,
      String param3) {

    log.debug("prompt2 called with params: {}, {}, {}", param1, param2, param3);
    return "prompt2 is called";
  }
}
