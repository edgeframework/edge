package org.edgeframework.eventbus.test;

import org.junit.Test;
import org.vertx.java.testframework.TestBase;

public class EventBusTests extends TestBase {

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    startApp(EventBusTestClient.class.getName());
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  @Test public void testSend() { startTest(getMethodName()); }
  @Test public void testReceive() { startTest(getMethodName()); }
  @Test public void testBothEnds() { startTest(getMethodName()); }
  @Test public void testReply() { startTest(getMethodName()); }
  @Test public void testMultipleParameters() { startTest(getMethodName()); }
  
  @Test public void testParameterTypes1() { startTest(getMethodName()); }
  @Test public void testParameterTypes2() { startTest(getMethodName()); }
  @Test public void testParameterTypes3() { startTest(getMethodName()); }
  @Test public void testParameterTypes4() { startTest(getMethodName()); }
}
