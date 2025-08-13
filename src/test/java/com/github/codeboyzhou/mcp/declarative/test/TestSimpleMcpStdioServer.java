package com.github.codeboyzhou.mcp.declarative.test;

import com.github.codeboyzhou.mcp.declarative.McpServers;
import com.github.codeboyzhou.mcp.declarative.server.simple.SimpleMcpServerBaseInfo;
import java.time.Duration;

public class TestSimpleMcpStdioServer {

  public static void main(String[] args) {
    SimpleMcpServerBaseInfo info =
        SimpleMcpServerBaseInfo.builder()
            .name("mcp-server")
            .version("1.0.0")
            .instructions("test")
            .requestTimeout(Duration.ofSeconds(10))
            .build();
    McpServers.run(TestSimpleMcpStdioServer.class, args).startStdioServer(info);
  }
}
