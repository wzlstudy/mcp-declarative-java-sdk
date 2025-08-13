package com.github.codeboyzhou.mcp.declarative.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.codeboyzhou.mcp.declarative.exception.McpServerException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ObjectMappersTest {

  static class CircularReference {
    private final CircularReference self = this;
  }

  record Person(String name, int age) {}

  @Test
  void testConstructor_shouldThrowException() {
    assertThrows(UnsupportedOperationException.class, ObjectMappers::new);
  }

  @Test
  void testToJson_shouldSucceed() {
    String json = ObjectMappers.toJson(new Person("test", 25));
    assertTrue(json.contains("\"name\":\"test\""));
    assertTrue(json.contains("\"age\":25"));
  }

  @Test
  void testToJson_shouldThrowException() throws Exception {
    assertThrows(McpServerException.class, () -> ObjectMappers.toJson(new CircularReference()));
  }

  @Test
  void testFromYaml_shouldSucceed() throws IOException {
    String yamlContent = "name: test\nage: 25";
    File tempYaml = File.createTempFile("test", ".yaml");
    try (FileWriter writer = new FileWriter(tempYaml)) {
      writer.write(yamlContent);
    }
    Person person = ObjectMappers.fromYaml(tempYaml, Person.class);
    assertEquals("test", person.name);
    assertEquals(25, person.age);
  }

  @Test
  void testFromYaml_shouldThrowException() {
    File file = new File("non-existent.yaml");
    assertThrows(McpServerException.class, () -> ObjectMappers.fromYaml(file, Map.class));
  }
}
