package com.github.codeboyzhou.mcp.declarative.test;

import com.github.codeboyzhou.mcp.declarative.McpServers;
import com.github.codeboyzhou.mcp.declarative.server.McpServerInfo;
import java.time.Duration;

public class TestSimpleMcpStdioServer {

  public static void main(String[] args) {
    McpServerInfo info =
        McpServerInfo.builder()
            .name("mcp-server")
            .version("1.0.0")
            .instructions("test")
            .requestTimeout(Duration.ofSeconds(10))
            .build();
    McpServers.run(TestSimpleMcpStdioServer.class, args).startStdioServer(info);
  }
}
