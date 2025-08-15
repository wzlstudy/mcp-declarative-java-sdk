package com.github.codeboyzhou.mcp.declarative.exception;

public class McpServerConfigurationException extends McpServerException {
  public McpServerConfigurationException(String message) {
    super(message);
  }

  public McpServerConfigurationException(String message, Throwable cause) {
    super(message, cause);
  }
}
