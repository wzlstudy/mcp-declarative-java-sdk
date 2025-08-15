package com.github.codeboyzhou.mcp.declarative.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.codeboyzhou.mcp.declarative.enums.ServerMode;
import com.github.codeboyzhou.mcp.declarative.enums.ServerType;

public record McpServerConfiguration(
    @JsonProperty("enabled") boolean enabled,
    @Deprecated(since = "0.7.0", forRemoval = true) @JsonProperty("stdio") boolean stdio,
    @JsonProperty("mode") ServerMode mode,
    @JsonProperty("name") String name,
    @JsonProperty("version") String version,
    @JsonProperty("type") ServerType type,
    @JsonProperty("instructions") String instructions,
    @JsonProperty("request-timeout") long requestTimeout,
    @JsonProperty("capabilities") McpServerCapabilities capabilities,
    @JsonProperty("change-notification") McpServerChangeNotification changeNotification,
    @JsonProperty("sse") McpServerSSE sse) {}
