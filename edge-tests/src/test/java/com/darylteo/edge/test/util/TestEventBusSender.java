package com.darylteo.edge.test.util;

import org.edgeframework.promises.Promise;

public interface TestEventBusSender {
  public Promise<String> testString(String message);
}