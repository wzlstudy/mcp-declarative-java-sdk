package com.github.codeboyzhou.mcp.declarative.common;

import com.github.codeboyzhou.mcp.declarative.util.ObjectMappers;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class BufferQueue<T> {

  private static final Logger logger = LoggerFactory.getLogger(BufferQueue.class);

  private static final int DEFAULT_DELAYED_CONSUMPTION_MILLIS = 10;

  private final BlockingQueue<T> queue = new LinkedBlockingQueue<>();

  private final long delayMillis;

  public BufferQueue(long delayMillis) {
    if (delayMillis <= 0) {
      throw new IllegalArgumentException("delayMillis must be greater than 0");
    }
    this.delayMillis = delayMillis;
  }

  public BufferQueue() {
    this(DEFAULT_DELAYED_CONSUMPTION_MILLIS);
  }

  public void submit(T component) {
    try {
      queue.put(component);
      logger.debug("Component submitted to queue: {}", ObjectMappers.toJson(component));
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  public void consume(Consumer<T> consumer) {
    NamedThreadFactory threadFactory = new NamedThreadFactory(getClass().getSimpleName());
    Executors.newSingleThreadExecutor(threadFactory)
        .execute(
            () -> {
              try {
                while (!Thread.interrupted()) {
                  T component = queue.take();
                  consumer.accept(component);
                  logger.debug(
                      "Component consumed from queue: {}", ObjectMappers.toJson(component));
                  TimeUnit.MILLISECONDS.sleep(delayMillis);
                }
              } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
              }
            });
  }
}
