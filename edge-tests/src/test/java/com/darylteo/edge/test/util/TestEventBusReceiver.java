package com.darylteo.edge.test.util;

import org.edgeframework.eventbus.EventBusParams;

public interface TestEventBusReceiver {
  @EventBusParams({"message"})
  public void testString(String message);
  
  @EventBusParams({"message"})
  public String testReply(String message);
}