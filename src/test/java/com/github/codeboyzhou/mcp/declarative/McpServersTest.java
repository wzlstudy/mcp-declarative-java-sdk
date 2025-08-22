package com.github.codeboyzhou.mcp.declarative;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.github.codeboyzhou.mcp.declarative.configuration.McpServerConfiguration;
import com.github.codeboyzhou.mcp.declarative.configuration.YAMLConfigurationLoader;
import com.github.codeboyzhou.mcp.declarative.enums.ServerMode;
import com.github.codeboyzhou.mcp.declarative.exception.McpServerConfigurationException;
import com.github.codeboyzhou.mcp.declarative.server.factory.McpSseServerInfo;
import com.github.codeboyzhou.mcp.declarative.server.factory.McpStreamableServerInfo;
import com.github.codeboyzhou.mcp.declarative.test.TestSimpleMcpStdioServer;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport;
import io.modelcontextprotocol.client.transport.HttpClientStreamableHttpTransport;
import io.modelcontextprotocol.client.transport.ServerParameters;
import io.modelcontextprotocol.client.transport.StdioClientTransport;
import io.modelcontextprotocol.spec.McpSchema;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.junit.jupiter.api.Test;

class McpServersTest {

  McpServers servers = McpServers.run(McpServersTest.class, new String[] {});

  @Test
  void testStartStdioServer_shouldSucceed() {
    TestSimpleMcpStdioServer.main(new String[] {}); // just for jacoco coverage report

    String classpath = System.getProperty("java.class.path");

    ServerParameters serverParameters =
        ServerParameters.builder("java")
            .args("-cp", classpath, TestSimpleMcpStdioServer.class.getName())
            .build();

    StdioClientTransport stdioClientTransport = new StdioClientTransport(serverParameters);

    try (McpSyncClient client = McpClient.sync(stdioClientTransport).build()) {
      verify(client);
    }
  }

  @Test
  void testStartSseServer_shouldSucceed() {
    final int port = new Random().nextInt(8000, 9000);

    McpSseServerInfo serverInfo =
        McpSseServerInfo.builder()
            .name("mcp-server")
            .version("1.0.0")
            .instructions("test")
            .requestTimeout(Duration.ofSeconds(10))
            .baseUrl("http://localhost:" + port)
            .port(port)
            .sseEndpoint("/sse")
            .messageEndpoint("/mcp/message")
            .build();

    HttpClientSseClientTransport transport =
        HttpClientSseClientTransport.builder("http://localhost:" + port)
            .sseEndpoint("/sse")
            .build();

    servers.startSseServer(serverInfo);

    try (McpSyncClient client = McpClient.sync(transport).build()) {
      verify(client);
    }
  }

  @Test
  void testStartStreamableServer_shouldSucceed() {
    final int port = new Random().nextInt(8000, 9000);

    McpStreamableServerInfo serverInfo =
        McpStreamableServerInfo.builder()
            .name("mcp-server")
            .version("1.0.0")
            .instructions("test")
            .requestTimeout(Duration.ofSeconds(10))
            .port(port)
            .mcpEndpoint("/mcp/message")
            .build();

    HttpClientStreamableHttpTransport transport =
        HttpClientStreamableHttpTransport.builder("http://localhost:" + port)
            .endpoint("/mcp/message")
            .build();

    servers.startStreamableServer(serverInfo);

    try (McpSyncClient client = McpClient.sync(transport).build()) {
      verify(client);
    }
  }

  @Test
  void testStartServer_disabledMCP_shouldSucceed() {
    String configFileName = "test-mcp-server-disabled.yml";
    YAMLConfigurationLoader configLoader = new YAMLConfigurationLoader(configFileName);
    McpServerConfiguration configuration = configLoader.loadConfig();
    assertDoesNotThrow(() -> servers.startServer(configFileName));
    assertFalse(configuration.enabled());
  }

  @Test
  void testStartServer_enableStdioMode_shouldSucceed() {
    String configFileName = "test-mcp-server-enable-stdio-mode.yml";
    YAMLConfigurationLoader configLoader = new YAMLConfigurationLoader(configFileName);
    McpServerConfiguration configuration = configLoader.loadConfig();
    assertDoesNotThrow(() -> servers.startServer(configFileName));
    assertSame(ServerMode.STDIO, configuration.mode());
  }

  @Test
  void testStartServer_enableHttpSseMode_shouldSucceed() {
    String configFileName = "test-mcp-server-enable-http-sse-mode.yml";
    YAMLConfigurationLoader configLoader = new YAMLConfigurationLoader(configFileName);
    McpServerConfiguration configuration = configLoader.loadConfig();
    assertDoesNotThrow(() -> servers.startServer(configFileName));
    assertSame(ServerMode.SSE, configuration.mode());
  }

  @Test
  void testStartServer_enableStreamableHttpMode_shouldSucceed() {
    String configFileName = "test-mcp-server-enable-streamable-http-mode.yml";
    YAMLConfigurationLoader configLoader = new YAMLConfigurationLoader(configFileName);
    McpServerConfiguration configuration = configLoader.loadConfig();
    assertDoesNotThrow(() -> servers.startServer(configFileName));
    assertSame(ServerMode.STREAMABLE, configuration.mode());
  }

  @Test
  void testStartServer_enableUnknownMode_shouldThrowException() {
    String configFileName = "test-mcp-server-enable-unknown-mode.yml";
    assertThrows(McpServerConfigurationException.class, () -> servers.startServer(configFileName));
  }

  @Test
  void testStartServer_useDefaultConfigFileName_shouldSucceed() {
    String configFileName = "mcp-server.yml";
    YAMLConfigurationLoader configLoader = new YAMLConfigurationLoader(configFileName);
    McpServerConfiguration configuration = configLoader.loadConfig();
    assertSame(ServerMode.STREAMABLE, configuration.mode());
    assertDoesNotThrow(() -> servers.startServer());
  }

  private void verify(McpSyncClient client) {
    verifyServerInfo(client);
    verifyResourcesRegistered(client);
    verifyPromptsRegistered(client);
    verifyToolsRegistered(client);
    verifyPromptsCalled(client);
    verifyToolsCalled(client);
  }

  private void verifyServerInfo(McpSyncClient client) {
    McpSchema.InitializeResult initialized = client.initialize();
    assertEquals("mcp-server", initialized.serverInfo().name());
    assertEquals("1.0.0", initialized.serverInfo().version());
    assertEquals("test", initialized.instructions());
  }

  private void verifyResourcesRegistered(McpSyncClient client) {
    List<McpSchema.Resource> resources = client.listResources().resources();
    assertEquals(1, resources.size());
    McpSchema.Resource resource = resources.get(0);
    assertEquals("test://resource1", resource.uri());
    assertEquals("resource1_name", resource.name());
    assertEquals("resource1_title", resource.title());
    assertEquals("resource1_description", resource.description());
    assertEquals("text/plain", resource.mimeType());
  }

  private void verifyPromptsRegistered(McpSyncClient client) {
    List<McpSchema.Prompt> prompts = client.listPrompts().prompts();
    assertEquals(1, prompts.size());

    McpSchema.Prompt prompt = prompts.get(0);
    assertEquals("prompt1_name", prompt.name());
    assertEquals("prompt1_title", prompt.title());
    assertEquals("prompt1_description", prompt.description());

    List<McpSchema.PromptArgument> arguments = prompt.arguments();
    assertEquals(2, arguments.size());
    assertEquals("param1", arguments.get(0).name());
    assertEquals("param1_title", arguments.get(0).title());
    assertEquals("param1_description", arguments.get(0).description());
    assertEquals("param2", arguments.get(1).name());
    assertEquals("param2_title", arguments.get(1).title());
    assertEquals("param2_description", arguments.get(1).description());
  }

  private void verifyPromptsCalled(McpSyncClient client) {
    String name1 = "prompt1_name";
    Map<String, Object> args1 = Map.of("param1", "value1", "param2", "value2");
    McpSchema.GetPromptRequest request1 = new McpSchema.GetPromptRequest(name1, args1);
    McpSchema.GetPromptResult result1 = client.getPrompt(request1);
    McpSchema.TextContent content = (McpSchema.TextContent) result1.messages().get(0).content();
    assertEquals(args1.get("param1") + args1.get("param2").toString(), content.text());
    assertEquals("prompt1_description", result1.description());
  }

  private void verifyToolsRegistered(McpSyncClient client) {
    List<McpSchema.Tool> tools = client.listTools().tools();
    assertEquals(1, tools.size());
    McpSchema.Tool tool = tools.get(0);
    assertEquals("tool1_name", tool.name());
    assertEquals("tool1_title", tool.title());
    assertEquals("tool1_description", tool.description());
  }

  private void verifyToolsCalled(McpSyncClient client) {
    String name1 = "tool1_name";
    Map<String, Object> args1 = Map.of("param1", "value1", "param2", "value2");
    McpSchema.CallToolRequest request1 = new McpSchema.CallToolRequest(name1, args1);
    McpSchema.CallToolResult result1 = client.callTool(request1);
    McpSchema.TextContent content = (McpSchema.TextContent) result1.content().get(0);
    assertEquals(args1.get("param1") + args1.get("param2").toString(), content.text());
    assertFalse(result1.isError());
  }
}
