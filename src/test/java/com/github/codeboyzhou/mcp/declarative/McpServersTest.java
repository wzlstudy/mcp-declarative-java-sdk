package com.github.codeboyzhou.mcp.declarative;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.codeboyzhou.mcp.declarative.test.TestSimpleMcpStdioServer;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.ServerParameters;
import io.modelcontextprotocol.client.transport.StdioClientTransport;
import io.modelcontextprotocol.spec.McpSchema;
import java.util.List;
import org.junit.jupiter.api.Test;

class McpServersTest {

  String classpath = System.getProperty("java.class.path");

  ServerParameters stdioServerParameters =
      ServerParameters.builder("java")
          .args("-cp", classpath, TestSimpleMcpStdioServer.class.getName())
          .build();

  StdioClientTransport stdioClientTransport = new StdioClientTransport(stdioServerParameters);

  @Test
  void testStartStdioServer_shouldSucceed() {
    try (McpSyncClient client = McpClient.sync(stdioClientTransport).build()) {
      McpSchema.InitializeResult initialized = client.initialize();
      assertEquals("mcp-server", initialized.serverInfo().name());
      assertEquals("1.0.0", initialized.serverInfo().version());
      assertEquals("test", initialized.instructions());
    }
  }

  @Test
  void testStartStdioServer_shouldRegisteredResources() {
    try (McpSyncClient client = McpClient.sync(stdioClientTransport).build()) {
      client.initialize();
      List<McpSchema.Resource> resources = client.listResources().resources();
      assertEquals(1, resources.size());
      McpSchema.Resource resource = resources.get(0);
      assertEquals("test://resource1", resource.uri());
      assertEquals("resource1_name", resource.name());
      assertEquals("resource1_title", resource.title());
      assertEquals("resource1_description", resource.description());
      assertEquals("text/plain", resource.mimeType());
    }
  }

  @Test
  void testStartStdioServer_shouldRegisteredPrompts() {
    try (McpSyncClient client = McpClient.sync(stdioClientTransport).build()) {
      client.initialize();
      List<McpSchema.Prompt> prompts = client.listPrompts().prompts();
      assertEquals(1, prompts.size());
      McpSchema.Prompt prompt = prompts.get(0);
      assertEquals("prompt1_name", prompt.name());
      assertEquals("prompt1_title", prompt.title());
      assertEquals("prompt1_description", prompt.description());
      assertTrue(prompt.arguments().isEmpty());
    }
  }

  @Test
  void testStartStdioServer_shouldRegisteredTools() {
    try (McpSyncClient client = McpClient.sync(stdioClientTransport).build()) {
      client.initialize();
      List<McpSchema.Tool> tools = client.listTools().tools();
      assertEquals(1, tools.size());
      McpSchema.Tool tool = tools.get(0);
      assertEquals("tool1_name", tool.name());
      assertEquals("tool1_title", tool.title());
      assertEquals("tool1_description", tool.description());
      assertTrue(tool.inputSchema().properties().isEmpty());
    }
  }
}
