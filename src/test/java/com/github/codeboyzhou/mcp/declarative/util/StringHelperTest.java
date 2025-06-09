package com.github.codeboyzhou.mcp.declarative.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StringHelperTest {

    @Test
    void testNewInstance() {
        UnsupportedOperationException e = assertThrows(UnsupportedOperationException.class, StringHelper::new);
        assertEquals("Utility class should not be instantiated", e.getMessage());
    }

}
