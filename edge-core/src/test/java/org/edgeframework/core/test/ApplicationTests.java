package org.edgeframework.core.test;

import org.junit.Test;
import org.vertx.java.testframework.TestBase;

public class ApplicationTests extends TestBase {

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    startApp(ApplicationTestClient.class.getName());
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  @Test public void testBasicRoute1() { startTest(getMethodName()); }
  @Test public void testBasicRoute2() { startTest(getMethodName()); }

  @Test
  public void testMultipleHandlers() {
    startTest(getMethodName());
  }

  @Test
  public void testPost() {
    startTest(getMethodName());
  }
  
  @Test public void test404_1() { startTest(getMethodName()); }
  @Test public void test404_2() { startTest(getMethodName()); }
  
  @Test public void testRouteParams1() { startTest(getMethodName()); }
}
