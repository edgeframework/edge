package com.darylteo.edge.tests;

import org.junit.Test;
import org.vertx.java.framework.TestBase;

import com.darylteo.edge.test.client.PromiseTestClient;

public class PromiseTests extends TestBase {
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    startApp(PromiseTestClient.class.getName());
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  @Test public void testDefer() { perform(); }
  @Test public void testDefer2() { perform(); }
  
  @Test public void testBasic() { perform(); }
  @Test public void testPrefilled() { perform(); }
  
  @Test public void testChain1() { perform(); }
  @Test public void testChain2() { perform(); }
  
  @Test public void testException1() { perform(); }
  @Test public void testException2() { perform(); }
  @Test public void testException3() { perform(); }
  @Test public void testException4() { perform(); }
  @Test public void testException5() { exceptionTest(IllegalStateException.class); }
  @Test public void testException6() { exceptionTest(IllegalStateException.class); }
  
  @Test public void testFinally1() { perform(); }
  @Test public void testFinally2() { perform(); }
  
  private void perform(){
    startTest(getMethodName());
  }
  
  @SuppressWarnings("unused")
  private <T extends Throwable> void exceptionTest(){
    try{
      startTest(getMethodName());
      fail("Exception was not thrown");
    }finally{}
  }
  
  private <T extends Throwable> void exceptionTest(final Class<T> clazz){
    try{
      startTest(getMethodName());
      fail("Exception of type " + clazz.getName() + " was not thrown");
    }catch(Throwable e){
      assertTrue(e.getClass().equals(clazz));
    }
  }
  
  @Override
  public String getMethodName(){
    return Thread.currentThread().getStackTrace()[3].getMethodName();
  }
}
