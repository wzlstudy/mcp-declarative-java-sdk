package com.github.codeboyzhou.mcp.declarative.server.simple;

import io.modelcontextprotocol.server.McpTransportContextExtractor;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Duration;

public class SimpleMcpHttpStreamableServerInfo extends SimpleMcpServerBaseInfo {

  private final int port;

  private final String mcpEndpoint;

  private final boolean disallowDelete;

  private final McpTransportContextExtractor<HttpServletRequest> contextExtractor;

  private final Duration keepAliveInterval;

  private SimpleMcpHttpStreamableServerInfo(SimpleMcpHttpStreamableServerInfo.Builder builder) {
    super(builder);
    this.port = builder.port;
    this.mcpEndpoint = builder.mcpEndpoint;
    this.disallowDelete = builder.disallowDelete;
    this.contextExtractor = builder.contextExtractor;
    this.keepAliveInterval = builder.keepAliveInterval;
  }

  public static SimpleMcpHttpStreamableServerInfo.Builder builder() {
    return new SimpleMcpHttpStreamableServerInfo.Builder();
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

  public static class Builder
      extends SimpleMcpServerBaseInfo.Builder<SimpleMcpHttpStreamableServerInfo.Builder> {

    private int port = 8080;

    private String mcpEndpoint = "/mcp";

    private boolean disallowDelete = false;

    private McpTransportContextExtractor<HttpServletRequest> contextExtractor =
        (request, context) -> context;

    private Duration keepAliveInterval;

    @Override
    protected SimpleMcpHttpStreamableServerInfo.Builder self() {
      return this;
    }

    @Override
    public SimpleMcpHttpStreamableServerInfo build() {
      return new SimpleMcpHttpStreamableServerInfo(this);
    }

    public SimpleMcpHttpStreamableServerInfo.Builder port(int port) {
      this.port = port;
      return self();
    }

    public SimpleMcpHttpStreamableServerInfo.Builder mcpEndpoint(String mcpEndpoint) {
      this.mcpEndpoint = mcpEndpoint;
      return self();
    }

    public SimpleMcpHttpStreamableServerInfo.Builder disallowDelete(boolean disallowDelete) {
      this.disallowDelete = disallowDelete;
      return self();
    }

    public SimpleMcpHttpStreamableServerInfo.Builder contextExtractor(
        McpTransportContextExtractor<HttpServletRequest> contextExtractor) {
      this.contextExtractor = contextExtractor;
      return self();
    }

    public SimpleMcpHttpStreamableServerInfo.Builder keepAliveInterval(Duration keepAliveInterval) {
      this.keepAliveInterval = keepAliveInterval;
      return self();
    }
  }
}
