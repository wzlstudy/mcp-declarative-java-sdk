package com.github.codeboyzhou.mcp.declarative.exception;

public class McpServerException extends RuntimeException {

    public McpServerException(String message) {
        super(message);
    }

    public McpServerException(String message, Throwable cause) {
        super(message, cause);
    }

}
