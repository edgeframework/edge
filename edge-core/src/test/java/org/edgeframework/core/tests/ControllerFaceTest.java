package org.edgeframework.core.tests;

import org.edgeframework.core.tests.faces.AdminControllerFace;
import org.edgeframework.core.tests.util.AbstractFaceTest;
import org.junit.Test;

import com.darylteo.rx.promises.Promise;

public class ControllerFaceTest extends AbstractFaceTest {
  @Test
  public void testGetNoParams() {
    deployVerticle(AdminControllerFace.class.getCanonicalName())
      .then(testContents("localhost", 8081, "/index", "index"))
      .fail(onFailure())
      .fin(onComplete());
  }

  @Test
  public void testGetWithGetParams() {
    deployVerticle(AdminControllerFace.class.getCanonicalName())
      .then(testContents("localhost", 8081, "/index?get=test", "test"))
      .fail(onFailure())
      .fin(onComplete());
  }

  @Test
  public void testGetWithUrlParam() {
    Promise<String> promise = deployVerticle(AdminControllerFace.class.getCanonicalName());

    promise
      .then(testContents("localhost", 8081, "/index/part1", "part1"))
      .fail(onFailure())
      .fin(onComplete());

    promise
      .then(testContents("localhost", 8081, "/index/part1/part2", "part1:part2"))
      .fail(onFailure())
      .fin(onComplete());

    promise
      .then(testContents("localhost", 8081, "/index/part1/part2/part3", "part1:part2:part3"))
      .fail(onFailure())
      .fin(onComplete());
  }

}
