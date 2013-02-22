package org.edgeframework.controllers.test;

import org.junit.Test;
import org.vertx.java.testframework.TestBase;

public class ControllersTests extends TestBase {

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    startApp(ControllersTestClient.class.getName());
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  @Test public void testOkResult() { startTest(getMethodName()); }
  @Test public void testRenderResult() { startTest(getMethodName()); }
  @Test public void testJsonResult() { startTest(getMethodName()); }
  @Test public void testAsyncResult() { startTest(getMethodName()); }
  
  @Test public void testRouteParams1() { startTest(getMethodName()); }
  @Test public void testQueryString1() { startTest(getMethodName()); }
  @Test public void testBodyString1() { startTest(getMethodName()); }
}
