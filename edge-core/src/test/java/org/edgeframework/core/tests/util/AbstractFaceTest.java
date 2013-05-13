package org.edgeframework.core.tests.util;

import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpClientResponse;
import org.vertx.testtools.TestVerticle;

import com.darylteo.rx.promises.Promise;

public class AbstractFaceTest extends TestVerticle {
  protected Promise<String> deployVerticle(String main) {
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

  protected Promise<HttpClientResponse> getUrl(String host, int port, String path) {
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

  protected Promise<String> getPage(HttpClientResponse response) {
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