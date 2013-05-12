package org.edgeframework.core.tests;

import org.junit.Test;
import org.vertx.testtools.TestVerticle;
import org.vertx.testtools.VertxAssert;

public class FaceTest extends TestVerticle {
  @Override
  public void start() {
    // TODO Auto-generated method stub
    super.start();
  }

  @Override
  public void stop() {
    // TODO Auto-generated method stub
    super.stop();
  }

  @Test
  public void testFace() {
    container.deployVerticle("org.edgeframework.core.tests.StaticAssets");

    // VertxAssert.testComplete();
  }
}