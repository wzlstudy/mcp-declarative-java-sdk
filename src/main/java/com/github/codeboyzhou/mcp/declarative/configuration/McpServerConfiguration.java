package com.github.codeboyzhou.mcp.declarative.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public record McpServerConfiguration(
    @JsonProperty("enabled") boolean enabled,
    @JsonProperty("stdio") boolean stdio,
    @JsonProperty("name") String name,
    @JsonProperty("version") String version,
    @JsonProperty("instructions") String instructions,
    @JsonProperty("request-timeout") long requestTimeout,
    @JsonProperty("type") String type,
    @JsonProperty("resource-change-notification") boolean resourceChangeNotification,
    @JsonProperty("prompt-change-notification") boolean promptChangeNotification,
    @JsonProperty("tool-change-notification") boolean toolChangeNotification,
    @JsonProperty("tool-response-mime-type") Map<String, String> toolResponseMimeType,
    @JsonProperty("sse-message-endpoint") String sseMessageEndpoint,
    @JsonProperty("sse-endpoint") String sseEndpoint,
    @JsonProperty("base-url") String baseUrl,
    @JsonProperty("sse-port") int ssePort
) {

    public static McpServerConfiguration defaultConfiguration() {
        return new McpServerConfiguration(
            true,
            false,
            "mcp-server",
            "1.0.0",
            "mcp-server",
            10000,
            "SYNC",
            true,
            true,
            true,
            Map.of(),
            "/mcp/message",
            "/sse",
            "",
            8080
        );
    }

}
