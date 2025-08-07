package com.github.codeboyzhou.mcp.declarative.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class StringHelperTest {

  @Test
  void testNewInstance() {
    UnsupportedOperationException e =
        assertThrows(UnsupportedOperationException.class, StringHelper::new);
    assertEquals("Utility class should not be instantiated", e.getMessage());
  }
}
