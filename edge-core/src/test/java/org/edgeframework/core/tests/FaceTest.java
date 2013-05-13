package org.edgeframework.core.tests;

import org.junit.Test;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpClientResponse;
import org.vertx.testtools.TestVerticle;
import org.vertx.testtools.VertxAssert;

import com.darylteo.rx.promises.FinallyAction;
import com.darylteo.rx.promises.Promise;
import com.darylteo.rx.promises.PromiseAction;
import com.darylteo.rx.promises.RepromiseFunction;

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
  public void testStatic() {
    deployVerticle("org.edgeframework.core.tests.StaticAssets")
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

  private Promise<String> deployVerticle(String main) {
    final Promise<String> promise = Promise.defer();

    container.deployVerticle(main, new Handler<AsyncResult<String>>() {
      @Override
      public void handle(AsyncResult<String> event) {
        if (event.succeeded()) {
          promise.fulfill(event.result());
        } else {
          promise.fail(event.cause());
        }

      }
    });

    return promise;
  }

  private Promise<HttpClientResponse> getUrl(String host, int port, String path) {
    final Promise<HttpClientResponse> promise = Promise.defer();
    vertx.createHttpClient()
        .setHost(host)
        .setPort(port)
        .getNow(path, new Handler<HttpClientResponse>() {
          @Override
          public void handle(HttpClientResponse event) {
            promise.fulfill(event);
          }
        });

    return promise;
  }

  private Promise<String> getPage(HttpClientResponse response) {
    final Promise<String> promise = Promise.defer();

    response.bodyHandler(new Handler<Buffer>() {
      @Override
      public void handle(Buffer event) {
        promise.fulfill(event.toString());
      }
    });

    return promise;
  }
}