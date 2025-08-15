package com.github.codeboyzhou.mcp.declarative.exception;

public class McpServerJsonProcessingException extends McpServerException {
  public McpServerJsonProcessingException(String message) {
    super(message);
  }

  public McpServerJsonProcessingException(String message, Throwable cause) {
    super(message, cause);
  }
}
