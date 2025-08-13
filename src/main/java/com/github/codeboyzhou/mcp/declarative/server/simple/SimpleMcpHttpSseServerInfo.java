package com.github.codeboyzhou.mcp.declarative.server.simple;

import com.github.codeboyzhou.mcp.declarative.util.Strings;

public class SimpleMcpHttpSseServerInfo extends SimpleMcpServerBaseInfo {

  private final String baseUrl;

  private final String messageEndpoint;

  private final String sseEndpoint;

  private final int port;

  private SimpleMcpHttpSseServerInfo(SimpleMcpHttpSseServerInfo.Builder builder) {
    super(builder);
    this.baseUrl = builder.baseUrl;
    this.messageEndpoint = builder.messageEndpoint;
    this.sseEndpoint = builder.sseEndpoint;
    this.port = builder.port;
  }

  public static SimpleMcpHttpSseServerInfo.Builder builder() {
    return new SimpleMcpHttpSseServerInfo.Builder();
  }

  public String baseUrl() {
    return baseUrl;
  }

  public String messageEndpoint() {
    return messageEndpoint;
  }

  public String sseEndpoint() {
    return sseEndpoint;
  }

  public int port() {
    return port;
  }

  public static class Builder
      extends SimpleMcpServerBaseInfo.Builder<SimpleMcpHttpSseServerInfo.Builder> {

    private String baseUrl = Strings.EMPTY;

    private String messageEndpoint = "/mcp/message";

    private String sseEndpoint = "/sse";

    private int port = 8080;

    @Override
    protected SimpleMcpHttpSseServerInfo.Builder self() {
      return this;
    }

    @Override
    public SimpleMcpHttpSseServerInfo build() {
      return new SimpleMcpHttpSseServerInfo(this);
    }

    public SimpleMcpHttpSseServerInfo.Builder baseUrl(String baseUrl) {
      this.baseUrl = baseUrl;
      return self();
    }

    public SimpleMcpHttpSseServerInfo.Builder messageEndpoint(String messageEndpoint) {
      this.messageEndpoint = messageEndpoint;
      return self();
    }

    public SimpleMcpHttpSseServerInfo.Builder sseEndpoint(String sseEndpoint) {
      this.sseEndpoint = sseEndpoint;
      return self();
    }

    public SimpleMcpHttpSseServerInfo.Builder port(int port) {
      this.port = port;
      return self();
    }
  }
}
