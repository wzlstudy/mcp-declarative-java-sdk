package com.github.codeboyzhou.mcp.declarative.server.component;

import io.modelcontextprotocol.server.McpSyncServerExchange;
import java.util.function.BiFunction;

public interface McpServerComponentHandler<U, R> extends BiFunction<McpSyncServerExchange, U, R> {}
