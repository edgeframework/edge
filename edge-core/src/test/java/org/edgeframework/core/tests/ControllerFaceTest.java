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
    deploy()
      .then(testContents("localhost", 8081, "/mapping/part1", "part1"))
      .then(testContents("localhost", 8081, "/mapping/part1/part2", "part1:part2"))
      .then(testContents("localhost", 8081, "/mapping/part1/part2/part3", "part1:part2:part3"))
      .fail(onFailure())
      .fin(onComplete());
  }

  @Test
  public void testGetWithUrlParamDatatypes() {
    deploy()
      .then(testContents("localhost", 8081, "/types/byte/5", "5:byte"))
      .then(testContents("localhost", 8081, "/types/short/50", "50:short"))
      .then(testContents("localhost", 8081, "/types/int/500", "500:int"))
      .then(testContents("localhost", 8081, "/types/long/5000", "5000:long"))
      .then(testContents("localhost", 8081, "/types/float/3.333333", "3.33:float"))
      .then(testContents("localhost", 8081, "/types/double/3.333333", "3.33:double"))
      .then(testContents("localhost", 8081, "/types/date/2012-01-01", "Sun Jan 01 00:01:00 EST 2012:date"))
      .then(testContents("localhost", 8081, "/types/timestamp/1325336460000", "Sun Jan 01 00:01:00 EST 2012:date"))
      .then(testContents("localhost", 8081, "/types/uuid/8c063560-c114-11e2-8b8b-0800200c9a66", "8c063560-c114-11e2-8b8b-0800200c9a66:uuid"))
      .fail(onFailure())
      .fin(onComplete());
  }

  @Test
  public void testSession() {
    deploy()
      .then(testContents("localhost", 8081, "/session/hello", "session:hello"))
      .then(testContents("localhost", 8081, "/session/", "session:hello"))
      .fail(onFailure())
      .fin(onComplete());
  }

  @Test
  public void testCookies() {
    deploy()
      .then(testContents("localhost", 8081, "/cookies/hello", "cookie:hello"))
      .then(testContents("localhost", 8081, "/cookies/", "cookie:hello"))
      .fail(onFailure())
      .fin(onComplete());

  }

  private Promise<String> deploy() {
    return deployVerticle(AdminControllerFace.class.getCanonicalName());
  }

}
