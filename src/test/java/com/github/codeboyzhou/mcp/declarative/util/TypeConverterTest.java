package com.github.codeboyzhou.mcp.declarative.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class TypeConverterTest {

  @Test
  void testNewInstance() {
    UnsupportedOperationException e =
        assertThrows(UnsupportedOperationException.class, TypeConverter::new);
    assertEquals("Utility class should not be instantiated", e.getMessage());
  }

  @Test
  void testConvertToTargetType() {
    assertEquals(StringHelper.EMPTY, TypeConverter.convert(null, String.class));
    assertEquals(0, TypeConverter.convert(null, int.class));
    assertEquals(0, TypeConverter.convert(null, Integer.class));
    assertEquals(0L, TypeConverter.convert(null, long.class));
    assertEquals(0L, TypeConverter.convert(null, Long.class));
    assertEquals(0.0f, TypeConverter.convert(null, float.class));
    assertEquals(0.0f, TypeConverter.convert(null, Float.class));
    assertEquals(0.0, TypeConverter.convert(null, double.class));
    assertEquals(0.0, TypeConverter.convert(null, Double.class));
    assertEquals(false, TypeConverter.convert(null, boolean.class));
    assertEquals(false, TypeConverter.convert(null, Boolean.class));
    assertNull(TypeConverter.convert(null, TypeConverterTest.class));

    assertEquals("123", TypeConverter.convert("123", String.class));
    assertEquals(123, TypeConverter.convert("123", int.class));
    assertEquals(123, TypeConverter.convert("123", Integer.class));
    assertEquals(123L, TypeConverter.convert("123", long.class));
    assertEquals(123L, TypeConverter.convert("123", Long.class));
    assertEquals(123.0f, TypeConverter.convert("123", float.class));
    assertEquals(123.0f, TypeConverter.convert("123", Float.class));
    assertEquals(123.0, TypeConverter.convert("123", double.class));
    assertEquals(123.0, TypeConverter.convert("123", Double.class));
    assertEquals(true, TypeConverter.convert("true", boolean.class));
    assertEquals(true, TypeConverter.convert("true", Boolean.class));
    assertEquals("123", TypeConverter.convert("123", TypeConverterTest.class));
  }

  @Test
  void testConvertToJsonSchemaType() {
    assertEquals(StringHelper.EMPTY, TypeConverter.convert(null, "string"));
    assertEquals(0, TypeConverter.convert(null, "integer"));
    assertEquals(0.0, TypeConverter.convert(null, "number"));
    assertEquals(false, TypeConverter.convert(null, "boolean"));
    assertNull(TypeConverter.convert(null, "object"));

    assertEquals("123", TypeConverter.convert("123", "string"));
    assertEquals(123, TypeConverter.convert("123", "integer"));
    assertEquals(123.0, TypeConverter.convert("123", "number"));
    assertEquals(true, TypeConverter.convert("true", "boolean"));
    assertEquals("123", TypeConverter.convert("123", "object"));
  }
}
