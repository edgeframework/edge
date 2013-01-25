package com.darylteo.edge.test.util;

import org.edgeframework.eventbus.EventBusParams;
import org.edgeframework.promises.Promise;

public interface TestEventBusSender {
  @EventBusParams({ "message" })
  public void testString(String message);

  @EventBusParams({ "message" })
  public Promise<String> testReply(String message);
}