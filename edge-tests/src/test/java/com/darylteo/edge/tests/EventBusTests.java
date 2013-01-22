package com.darylteo.edge.tests;

import org.junit.Test;
import org.vertx.java.framework.TestBase;

import com.darylteo.edge.test.client.EventBusTestClient;

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
}
