package org.edgeframework.core.tests;

import org.edgeframework.core.tests.faces.AdminController;
import org.edgeframework.core.tests.util.AbstractFaceTest;
import org.junit.Test;
import org.vertx.testtools.VertxAssert;

import com.darylteo.rx.promises.FinallyAction;
import com.darylteo.rx.promises.PromiseAction;

public class ControllerFaceTest extends AbstractFaceTest {
  @Test
  public void testGetNoParams() {
    deployVerticle(AdminController.class.getCanonicalName())
      .then(testContents("localhost", 8081, "/index", "index"))
      .fail(onFailure())
      .fin(onComplete());
  }

  @Test
  public void testGetWithParams() {
    deployVerticle(AdminController.class.getCanonicalName())
      .then(testContents("localhost", 8081, "/index?query=test", "test"))
      .fail(onFailure())
      .fin(onComplete());
  }
}
