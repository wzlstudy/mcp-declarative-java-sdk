package com.github.codeboyzhou.mcp.declarative.server.factory.configurable;

import com.github.codeboyzhou.mcp.declarative.configuration.McpServerConfiguration;

public final class ConfigurableMcpServerFactory {
  private ConfigurableMcpServerFactory() {
    // Using singleton design pattern should have private constructor
  }

  public static AbstractConfigurableMcpServer getServer(McpServerConfiguration config) {
    return switch (config.mode()) {
      case STDIO -> new ConfigurableMcpStdioServer(config);
      case SSE -> new ConfigurableMcpSseServer(config);
      case STREAMABLE -> new ConfigurableMcpStreamableServer(config);
    };
  }
}
