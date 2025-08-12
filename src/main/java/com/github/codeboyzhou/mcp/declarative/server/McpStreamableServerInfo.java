package com.github.codeboyzhou.mcp.declarative.server;

import io.modelcontextprotocol.server.McpTransportContextExtractor;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Duration;

public class McpStreamableServerInfo extends McpServerInfo {

  private final int port;

  private final String mcpEndpoint;

  private final boolean disallowDelete;

  private final McpTransportContextExtractor<HttpServletRequest> contextExtractor;

  private final Duration keepAliveInterval;

  private McpStreamableServerInfo(McpStreamableServerInfo.Builder builder) {
    super(builder);
    this.port = builder.port;
    this.mcpEndpoint = builder.mcpEndpoint;
    this.disallowDelete = builder.disallowDelete;
    this.contextExtractor = builder.contextExtractor;
    this.keepAliveInterval = builder.keepAliveInterval;
  }

  public static McpStreamableServerInfo.Builder builder() {
    return new McpStreamableServerInfo.Builder();
  }

  public int port() {
    return port;
  }

  public String mcpEndpoint() {
    return mcpEndpoint;
  }

  public boolean disallowDelete() {
    return disallowDelete;
  }

  public McpTransportContextExtractor<HttpServletRequest> contextExtractor() {
    return contextExtractor;
  }

  public Duration keepAliveInterval() {
    return keepAliveInterval;
  }

  public static class Builder extends McpServerInfo.Builder<McpStreamableServerInfo.Builder> {

    private int port;

    private String mcpEndpoint = "/mcp";

    private boolean disallowDelete = false;

    private McpTransportContextExtractor<HttpServletRequest> contextExtractor =
        (request, context) -> context;

    private Duration keepAliveInterval;

    @Override
    protected McpStreamableServerInfo.Builder self() {
      return this;
    }

    @Override
    public McpStreamableServerInfo build() {
      return new McpStreamableServerInfo(this);
    }

    public McpStreamableServerInfo.Builder port(int port) {
      this.port = port;
      return self();
    }

    public McpStreamableServerInfo.Builder mcpEndpoint(String mcpEndpoint) {
      this.mcpEndpoint = mcpEndpoint;
      return self();
    }

    public McpStreamableServerInfo.Builder disallowDelete(boolean disallowDelete) {
      this.disallowDelete = disallowDelete;
      return self();
    }

    public McpStreamableServerInfo.Builder contextExtractor(
        McpTransportContextExtractor<HttpServletRequest> contextExtractor) {
      this.contextExtractor = contextExtractor;
      return self();
    }

    public McpStreamableServerInfo.Builder keepAliveInterval(Duration keepAliveInterval) {
      this.keepAliveInterval = keepAliveInterval;
      return self();
    }
  }
}
