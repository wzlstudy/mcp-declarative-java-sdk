package com.github.codeboyzhou.mcp.declarative.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

public record McpServerCapabilities(
    @JsonProperty("resource") boolean resource,
    @JsonProperty("prompt") boolean prompt,
    @JsonProperty("tool") boolean tool) {}
