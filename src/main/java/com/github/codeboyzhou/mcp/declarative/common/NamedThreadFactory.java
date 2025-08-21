package com.github.codeboyzhou.mcp.declarative.common;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class NamedThreadFactory implements ThreadFactory {

  private static final Logger log = LoggerFactory.getLogger(NamedThreadFactory.class);

  private static final AtomicInteger poolNumber = new AtomicInteger(1);

  private final AtomicInteger threadNumber = new AtomicInteger(1);

  private final String namePrefix;

  public NamedThreadFactory(String namePrefix) {
    this.namePrefix = namePrefix + "-" + poolNumber.getAndIncrement() + "-thread-";
  }

  @Override
  public Thread newThread(@NotNull Runnable runnable) {
    Thread thread = new Thread(runnable, namePrefix + threadNumber.getAndIncrement());
    thread.setUncaughtExceptionHandler(this::handleUncaughtException);
    thread.setDaemon(true);
    return thread;
  }

  private void handleUncaughtException(Thread t, Throwable e) {
    log.error("Thread {} uncaught exception", t.getName(), e);
  }
}
