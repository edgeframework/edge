package org.edgeframework.core.tests;

import org.edgeframework.core.tests.faces.AdminControllerFace;
import org.edgeframework.core.tests.util.AbstractFaceTest;
import org.junit.Test;

public class ControllerFaceTest extends AbstractFaceTest {
  @Test
  public void testGetNoParams() {
    deployVerticle(AdminControllerFace.class.getCanonicalName())
      .then(testContents("localhost", 8081, "/index", "index"))
      .fail(onFailure())
      .fin(onComplete());
  }

  @Test
  public void testGetWithParams() {
    deployVerticle(AdminControllerFace.class.getCanonicalName())
      .then(testContents("localhost", 8081, "/index?query=test", "test"))
      .fail(onFailure())
      .fin(onComplete());
  }
}
