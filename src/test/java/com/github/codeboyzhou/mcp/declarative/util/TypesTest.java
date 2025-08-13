package com.github.codeboyzhou.mcp.declarative.util;

import static org.junit.jupiter.api.Assertions.*;

import com.github.codeboyzhou.mcp.declarative.enums.JsonSchemaDataType;
import org.junit.jupiter.api.Test;

class TypesTest {

  @Test
  void testConstructor_shouldThrowException() {
    assertThrows(UnsupportedOperationException.class, Types::new);
  }

  @Test
  void testConvertTargetType_shouldReturnDefaultValueWhenValueIsNull() {
    assertEquals(Strings.EMPTY, Types.convert(null, String.class));
    assertEquals(0, Types.convert(null, int.class));
    assertEquals(0, Types.convert(null, Integer.class));
    assertEquals(0L, Types.convert(null, long.class));
    assertEquals(0L, Types.convert(null, Long.class));
    assertEquals(0.0f, Types.convert(null, float.class));
    assertEquals(0.0f, Types.convert(null, Float.class));
    assertEquals(0.0, Types.convert(null, double.class));
    assertEquals(0.0, Types.convert(null, Double.class));
    assertEquals(false, Types.convert(null, boolean.class));
    assertEquals(false, Types.convert(null, Boolean.class));
    assertNull(Types.convert(null, Object.class));
  }

  @Test
  void testConvertTargetType_shouldReturnStrWhenTargetTypeIsStr() {
    assertEquals("test", Types.convert("test", String.class));
  }

  @Test
  void testConvertTargetType_shouldReturnIntWhenTargetTypeIsInt() {
    assertEquals(1, Types.convert("1", int.class));
    assertEquals(1, Types.convert("1", Integer.class));
  }

  @Test
  void testConvertTargetType_shouldReturnLongWhenTargetTypeIsLong() {
    assertEquals(1L, Types.convert("1", long.class));
    assertEquals(1L, Types.convert("1", Long.class));
  }

  @Test
  void testConvertTargetType_shouldReturnFloatWhenTargetTypeIsFloat() {
    assertEquals(1.0f, Types.convert("1", float.class));
    assertEquals(1.0f, Types.convert("1", Float.class));
  }

  @Test
  void testConvertTargetType_shouldReturnDoubleWhenTargetTypeIsDouble() {
    assertEquals(1.0, Types.convert("1", double.class));
    assertEquals(1.0, Types.convert("1", Double.class));
  }

  @Test
  void testConvertTargetType_shouldReturnBooleanWhenTargetTypeIsBoolean() {
    assertEquals(true, Types.convert("true", boolean.class));
    assertEquals(true, Types.convert("true", Boolean.class));
  }

  @Test
  void testConvertTargetType_shouldReturnValueAsStringWhenTargetTypeIsNotSupported() {
    assertEquals("test", Types.convert("test", Object.class));
  }

  @Test
  void testConvertJsonSchemaType_shouldReturnDefaultValueWhenValueIsNull() {
    assertEquals(Strings.EMPTY, Types.convert(null, JsonSchemaDataType.STRING.getType()));
    assertEquals(0, Types.convert(null, JsonSchemaDataType.INTEGER.getType()));
    assertEquals(0.0, Types.convert(null, JsonSchemaDataType.NUMBER.getType()));
    assertEquals(false, Types.convert(null, JsonSchemaDataType.BOOLEAN.getType()));
    assertNull(Types.convert(null, JsonSchemaDataType.OBJECT.getType()));
  }

  @Test
  void testConvertJsonSchemaType_shouldReturnDefaultValueWhenJsonSchemaTypeIsString() {
    assertEquals("test", Types.convert("test", JsonSchemaDataType.STRING.getType()));
  }

  @Test
  void testConvertJsonSchemaType_shouldReturnDefaultValueWhenJsonSchemaTypeIsInteger() {
    assertEquals(1, Types.convert("1", JsonSchemaDataType.INTEGER.getType()));
  }

  @Test
  void testConvertJsonSchemaType_shouldReturnDefaultValueWhenJsonSchemaTypeIsNumber() {
    assertEquals(1.0, Types.convert("1", JsonSchemaDataType.NUMBER.getType()));
  }

  @Test
  void testConvertJsonSchemaType_shouldReturnDefaultValueWhenJsonSchemaTypeIsBoolean() {
    assertEquals(true, Types.convert("true", JsonSchemaDataType.BOOLEAN.getType()));
  }

  @Test
  void testConvertJsonSchemaType_shouldReturnValueAsStringWhenJsonSchemaTypeIsNotSupported() {
    assertEquals("test", Types.convert("test", JsonSchemaDataType.OBJECT.getType()));
  }
}
