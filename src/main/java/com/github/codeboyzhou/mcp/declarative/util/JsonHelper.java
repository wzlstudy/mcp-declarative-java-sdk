package com.github.codeboyzhou.mcp.declarative.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.codeboyzhou.mcp.declarative.exception.McpServerException;
import org.jetbrains.annotations.VisibleForTesting;

public final class JsonHelper {

    public static final ObjectMapper MAPPER = new ObjectMapper();

    @VisibleForTesting
    JsonHelper() {
        throw new UnsupportedOperationException("Utility class should not be instantiated");
    }

    public static String toJson(Object object) {
        try {
            return MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new McpServerException("Error converting object to JSON", e);
        }
    }

}
