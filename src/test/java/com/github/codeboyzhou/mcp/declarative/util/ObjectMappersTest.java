package com.github.codeboyzhou.mcp.declarative.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.github.codeboyzhou.mcp.declarative.exception.McpServerException;
import org.junit.jupiter.api.Test;

class ObjectMappersTest {

  record TestClass(String name, int age) {}

  @Test
  void testNewInstance() {
    UnsupportedOperationException e =
        assertThrows(UnsupportedOperationException.class, ObjectMappers::new);
    assertEquals("Utility class should not be instantiated", e.getMessage());
  }

  @Test
  void testToJson() {
    assertDoesNotThrow(
        () -> {
          TestClass testObject = new TestClass("test", 18);
          assertEquals("{\"name\":\"test\",\"age\":18}", ObjectMappers.toJson(testObject));
        });

    McpServerException e = assertThrows(McpServerException.class, () -> ObjectMappers.toJson(this));
    assertEquals("Error converting object to JSON", e.getMessage());
  }
}
