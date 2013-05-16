package org.edgeframework.core.tests;

import org.edgeframework.core.tests.faces.AdminControllerFace;
import org.edgeframework.core.tests.util.AbstractFaceTest;
import org.junit.Test;

import com.darylteo.rx.promises.Promise;

public class ControllerFaceTest extends AbstractFaceTest {
  @Test
  public void testGetNoParams() {
    deploy()
      .then(testContents("localhost", 8081, "/echo", "index"))
      .fail(onFailure())
      .fin(onComplete());
  }

  @Test
  public void testGetWithGetParams() {
    deploy()
      .then(testContents("localhost", 8081, "/echo?get=test", "test"))
      .fail(onFailure())
      .fin(onComplete());
  }

  @Test
  public void testGetWithUrlParam() {
    Promise<String> promise = deploy();

    promise
      .then(testContents("localhost", 8081, "/mapping/part1", "part1"))
      .fail(onFailure())
      .fin(onComplete());

    promise
      .then(testContents("localhost", 8081, "/mapping/part1/part2", "part1:part2"))
      .fail(onFailure())
      .fin(onComplete());

    promise
      .then(testContents("localhost", 8081, "/mapping/part1/part2/part3", "part1:part2:part3"))
      .fail(onFailure())
      .fin(onComplete());
  }

  @Test
  public void testGetWithUrlParamDatatypes() {
    Promise<String> promise = deploy();

    promise
      .then(testContents("localhost", 8081, "/types/byte/5", "5"))
      .fail(onFailure())
      .fin(onComplete());

    promise
      .then(testContents("localhost", 8081, "/types/short/50", "50"))
      .fail(onFailure())
      .fin(onComplete());

    promise
      .then(testContents("localhost", 8081, "/types/int/500", "500"))
      .fail(onFailure())
      .fin(onComplete());

    promise
      .then(testContents("localhost", 8081, "/types/long/5000", "5000"))
      .fail(onFailure())
      .fin(onComplete());

    promise
      .then(testContents("localhost", 8081, "/types/float/3.333333", "3.33"))
      .fail(onFailure())
      .fin(onComplete());

    promise
      .then(testContents("localhost", 8081, "/types/double/3.333333", "3.33"))
      .fail(onFailure())
      .fin(onComplete());

    promise
      .then(testContents("localhost", 8081, "/types/date/2012-01-01", "Sun Jan 01 00:01:00 EST 2012"))
      .fail(onFailure())
      .fin(onComplete());

    promise
      .then(testContents("localhost", 8081, "/types/timestamp/1325376000", "Sun Jan 01 00:01:00 EST 2012"))
      .fail(onFailure())
      .fin(onComplete());
  }

  private Promise<String> deploy() {
    return deployVerticle(AdminControllerFace.class.getCanonicalName());
  }

}
