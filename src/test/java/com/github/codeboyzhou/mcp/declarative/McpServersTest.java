package com.github.codeboyzhou.mcp.declarative;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.github.codeboyzhou.mcp.declarative.annotation.McpPrompts;
import com.github.codeboyzhou.mcp.declarative.annotation.McpResources;
import com.github.codeboyzhou.mcp.declarative.annotation.McpTools;
import com.github.codeboyzhou.mcp.declarative.common.GuiceInjectorModule;
import com.github.codeboyzhou.mcp.declarative.server.McpServerInfo;
import com.github.codeboyzhou.mcp.declarative.server.McpSseServerInfo;
import com.github.codeboyzhou.mcp.declarative.server.TestMcpComponentScanBasePackageClass;
import com.github.codeboyzhou.mcp.declarative.server.TestMcpComponentScanBasePackageString;
import com.github.codeboyzhou.mcp.declarative.server.TestMcpComponentScanDefault;
import com.github.codeboyzhou.mcp.declarative.server.TestMcpComponentScanIsNull;
import com.github.codeboyzhou.mcp.declarative.server.TestMcpPrompts;
import com.github.codeboyzhou.mcp.declarative.server.TestMcpResources;
import com.github.codeboyzhou.mcp.declarative.server.TestMcpTools;
import com.google.inject.Guice;
import com.google.inject.Injector;
import java.time.Duration;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

class McpServersTest {

  static final String[] EMPTY_ARGS = new String[] {};

  @BeforeEach
  void setUp() {
    System.setProperty("mcp.declarative.java.sdk.testing", "true");
  }

  @AfterEach
  void tearDown() {
    Injector injector =
        Guice.createInjector(new GuiceInjectorModule(TestMcpComponentScanIsNull.class));
    Reflections reflections = injector.getInstance(Reflections.class);
    assertNotNull(reflections);

    Map<String, Set<String>> scannedClasses =
        reflections.getStore().get(Scanners.TypesAnnotated.name());

    Set<String> scannedPromptClass = scannedClasses.get(McpPrompts.class.getName());
    assertEquals(1, scannedPromptClass.size());
    assertEquals(scannedPromptClass.iterator().next(), TestMcpPrompts.class.getName());

    Set<String> scannedResourceClass = scannedClasses.get(McpResources.class.getName());
    assertEquals(1, scannedResourceClass.size());
    assertEquals(scannedResourceClass.iterator().next(), TestMcpResources.class.getName());

    Set<String> scannedToolClass = scannedClasses.get(McpTools.class.getName());
    assertEquals(1, scannedToolClass.size());
    assertEquals(scannedToolClass.iterator().next(), TestMcpTools.class.getName());
  }

  @ParameterizedTest
  @ValueSource(
      classes = {
        TestMcpComponentScanIsNull.class,
        TestMcpComponentScanBasePackageString.class,
        TestMcpComponentScanBasePackageClass.class,
        TestMcpComponentScanDefault.class
      })
  void testRun(Class<?> applicationMainClass) {
    McpServers.run(applicationMainClass, EMPTY_ARGS);
  }

  @Test
  void testStartStdioServer() {
    assertDoesNotThrow(
        () -> {
          McpServers servers = McpServers.run(TestMcpComponentScanIsNull.class, EMPTY_ARGS);
          McpServerInfo serverInfo =
              McpServerInfo.builder()
                  .instructions("test-mcp-sync-stdio-server-instructions")
                  .requestTimeout(Duration.ofSeconds(20))
                  .name("test-mcp-sync-stdio-server")
                  .version("1.0.0")
                  .build();
          servers.startStdioServer(serverInfo);
        });
  }

  @Test
  void testStartSseServer() {
    McpServers servers = McpServers.run(TestMcpComponentScanIsNull.class, EMPTY_ARGS);
    assertDoesNotThrow(
        () -> {
          McpSseServerInfo serverInfo =
              McpSseServerInfo.builder()
                  .instructions("test-mcp-sync-sse-server-instructions")
                  .requestTimeout(Duration.ofSeconds(20))
                  .baseUrl("http://127.0.0.1:8080")
                  .messageEndpoint("/message")
                  .sseEndpoint("/sse")
                  .port(8080)
                  .name("test-mcp-sync-sse-server")
                  .version("1.0.0")
                  .build();
          servers.startSseServer(serverInfo);
        });
  }

  @Test
  void testStartServer() {
    assertDoesNotThrow(
        () -> {
          McpServers servers = McpServers.run(TestMcpComponentScanIsNull.class, EMPTY_ARGS);
          servers.startServer();
        });
  }

  @ParameterizedTest
  @ValueSource(
      strings = {"mcp-server-async.yml", "mcp-server-sse-mode.yml", "mcp-server-not-enabled.yml"})
  void testStartServerWithConfigFileName(String configFileName) {
    assertDoesNotThrow(
        () -> {
          McpServers servers = McpServers.run(TestMcpComponentScanIsNull.class, EMPTY_ARGS);
          servers.startServer(configFileName);
        });
  }
}
