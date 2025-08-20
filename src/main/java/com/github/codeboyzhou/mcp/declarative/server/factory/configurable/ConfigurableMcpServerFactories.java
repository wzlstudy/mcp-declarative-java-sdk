package com.github.codeboyzhou.mcp.declarative.server.factory.configurable;

import com.github.codeboyzhou.mcp.declarative.configuration.McpServerConfiguration;

public final class ConfigurableMcpServerFactories {
  private ConfigurableMcpServerFactories() {
    // Using singleton design pattern should have private constructor
  }

  public static AbstractConfigurableMcpServerFactory getFactory(McpServerConfiguration config) {
    return switch (config.mode()) {
      case STDIO -> new ConfigurableMcpStdioServerFactory(config);
      case SSE -> new ConfigurableMcpSseServerFactory(config);
      case STREAMABLE -> new ConfigurableMcpStreamableServerFactory(config);
    };
  }
}
