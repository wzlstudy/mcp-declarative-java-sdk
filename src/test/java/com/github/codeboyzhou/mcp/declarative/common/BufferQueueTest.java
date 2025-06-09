package com.github.codeboyzhou.mcp.declarative.common;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class BufferQueueTest {

    @Test
    void testNewInstance() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> new BufferQueue<>(0));
        assertEquals("delayMillis must be greater than 0", e.getMessage());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testSubmitCatchException() throws Exception {
        BlockingQueue<String> queueMock = mock(LinkedBlockingQueue.class);
        doThrow(InterruptedException.class).when(queueMock).put(anyString());

        BufferQueue<String> bufferQueue = new BufferQueue<>();
        Field queue = BufferQueue.class.getDeclaredField("queue");
        queue.setAccessible(true);
        queue.set(bufferQueue, queueMock);

        bufferQueue.submit("test");

        verify(queueMock).put("test");
    }

    @Test
    @SuppressWarnings("unchecked")
    void testConsumeCatchException() throws Exception {
        BlockingQueue<String> queueMock = mock(LinkedBlockingQueue.class);
        doThrow(InterruptedException.class).when(queueMock).take();

        BufferQueue<String> bufferQueue = new BufferQueue<>();
        Field queue = BufferQueue.class.getDeclaredField("queue");
        queue.setAccessible(true);
        queue.set(bufferQueue, queueMock);

        bufferQueue.consume(string -> {
            // do nothing
        });

        verify(queueMock).take();
    }

}
