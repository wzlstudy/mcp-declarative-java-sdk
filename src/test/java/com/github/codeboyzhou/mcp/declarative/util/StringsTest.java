package com.github.codeboyzhou.mcp.declarative.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class StringsTest {

  @Test
  void testConstructor_shouldThrowException() {
    assertThrows(UnsupportedOperationException.class, Strings::new);
  }

  @Test
  void testDefaultIfBlank_shouldReturnDefaultValueWhenStrIsNull() {
    assertEquals("default", Strings.defaultIfBlank(null, "default"));
  }

  @Test
  void testDefaultIfBlank_shouldReturnDefaultValueWhenStrIsBlank() {
    assertEquals("default", Strings.defaultIfBlank(Strings.SPACE, "default"));
  }

  @Test
  void testDefaultIfBlank_shouldReturnDefaultValueWhenStrIsEmpty() {
    assertEquals("default", Strings.defaultIfBlank(Strings.EMPTY, "default"));
  }

  @Test
  void testDefaultIfBlank_shouldReturnStrWhenStrIsNotBlank() {
    assertEquals("test", Strings.defaultIfBlank("test", "default"));
  }
}
