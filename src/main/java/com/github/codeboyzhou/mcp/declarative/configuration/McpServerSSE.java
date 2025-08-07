package com.github.codeboyzhou.mcp.declarative.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

public record McpServerSSE(
    @JsonProperty("message-endpoint") String messageEndpoint,
    @JsonProperty("endpoint") String endpoint,
    @JsonProperty("base-url") String baseUrl,
    @JsonProperty("port") int port) {}
