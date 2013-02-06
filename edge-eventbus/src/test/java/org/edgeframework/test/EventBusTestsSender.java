package org.edgeframework.test;

import org.edgeframework.eventbus.EventBusParams;
import org.edgeframework.promises.Promise;

public interface EventBusTestsSender {
  @EventBusParams({ "message" })
  public void testString(String message);

  @EventBusParams({ "message" })
  public Promise<String> testReply(String message);

  @EventBusParams({ "message", "integer" })
  public Promise<String> testMultipleParameters(String message, int integer);

  @EventBusParams({ "n1", "n2", "n3", "n4" })
  public void testParameterTypes1(byte n1, short n2, int n3, long n4);

  @EventBusParams({ "n1", "n2", "n3", "n4" })
  public void testParameterTypes2(Byte n1, Short n2, Integer n3, Long n4);

  @EventBusParams({ "f1", "f2" })
  public void testParameterTypes3(float n1, double n2);

  @EventBusParams({ "f1", "f2" })
  public void testParameterTypes4(Float n1, Double n2);
}