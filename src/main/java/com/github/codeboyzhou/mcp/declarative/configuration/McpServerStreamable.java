package com.github.codeboyzhou.mcp.declarative.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

public record McpServerStreamable(
    @JsonProperty("mcp-endpoint") String mcpEndpoint,
    @JsonProperty("disallow-delete") boolean disallowDelete,
    @JsonProperty("keep-alive-interval") long keepAliveInterval,
    @JsonProperty("port") int port) {}
