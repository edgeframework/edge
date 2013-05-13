package org.edgeframework.core.tests;

import org.edgeframework.core.tests.faces.StaticAssets;
import org.edgeframework.core.tests.util.AbstractFaceTest;
import org.junit.Test;
import org.vertx.java.core.http.HttpClientResponse;
import org.vertx.testtools.VertxAssert;

import com.darylteo.rx.promises.FinallyAction;
import com.darylteo.rx.promises.Promise;
import com.darylteo.rx.promises.PromiseAction;
import com.darylteo.rx.promises.RepromiseFunction;

public class StaticFaceTest extends AbstractFaceTest {
  @Test
  public void testGet() {
    deployVerticle(StaticAssets.class.getCanonicalName())
        .then(new RepromiseFunction<String, HttpClientResponse>() {
          @Override
          public Promise<HttpClientResponse> call(String deployID) {
            System.out.println("Static Face deployed: " + deployID);
            return getUrl("localhost", 8080, "/test.html");
          }
        })
        .then(new RepromiseFunction<HttpClientResponse, String>() {
          @Override
          public Promise<String> call(HttpClientResponse response) {
            if (response.statusCode() != 200) {
              throw new RuntimeException("Could not get static asset");
            }

            return getPage(response);
          }
        })
        .then(new PromiseAction<String>() {
          @Override
          public void call(String result) {
            VertxAssert.assertEquals("Content of static file does not match expected", "Hello World", result);
          }
        })
        .fail(new PromiseAction<Exception>() {
          @Override
          public void call(Exception reason) {
            VertxAssert.fail(reason.toString());
          }
        })
        .fin(new FinallyAction() {
          @Override
          public void call() {
            VertxAssert.testComplete();
          }
        });
  }

  @Test
  public void testValidPath() {
    deployVerticle(StaticAssets.class.getCanonicalName())
        .then(new RepromiseFunction<String, HttpClientResponse>() {
          @Override
          public Promise<HttpClientResponse> call(String deployID) {
            System.out.println("Static Face deployed: " + deployID);
            return getUrl("localhost", 8080, "/../test.html");
          }
        })
        .then(new RepromiseFunction<HttpClientResponse, String>() {
          @Override
          public Promise<String> call(HttpClientResponse response) {
            if (response.statusCode() != 500) {
              throw new RuntimeException("Did not get server error.");
            }

            return getPage(response);
          }
        })
        .fail(new PromiseAction<Exception>() {
          @Override
          public void call(Exception reason) {
            VertxAssert.fail(reason.toString());
          }
        })
        .fin(new FinallyAction() {
          @Override
          public void call() {
            VertxAssert.testComplete();
          }
        });
  }
}
