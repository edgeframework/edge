package com.darylteo.edge.test.util;

import org.edgeframework.promises.Promise;

public interface TestEventBusSender {
  public void testString(String message);

  public Promise<String> testReply(String message);
}