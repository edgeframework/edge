package org.edgeframework.core.tests.util;

import java.util.Collections;
import java.util.List;

import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpClientRequest;
import org.vertx.java.core.http.HttpClientResponse;
import org.vertx.testtools.TestVerticle;
import org.vertx.testtools.VertxAssert;

import com.darylteo.rx.promises.Promise;
import com.darylteo.rx.promises.functions.FinallyAction;
import com.darylteo.rx.promises.functions.PromiseAction;
import com.darylteo.rx.promises.functions.RepromiseFunction;

public class AbstractFaceTest extends TestVerticle {
  private List<String> cookies = Collections.emptyList();

  protected Promise<String> deployVerticle(String main) {
    System.out.println("Deploying " + main);
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

  protected RepromiseFunction<String, Void> testContents(final String host, final int port, final String path, final String expected) {
    return new RepromiseFunction<String, Void>() {
      @Override
      public Promise<Void> call(String deployID) {
        return getUrl(host, port, path)
          .then(new RepromiseFunction<HttpClientResponse, String>() {
            @Override
            public Promise<String> call(HttpClientResponse response) {
              cookies = response.cookies();
              return getPageContents(response);
            }
          })
          .then(new PromiseAction<String>() {
            @Override
            public void call(String contents) {
              VertxAssert.assertEquals(expected, contents);
            }
          });
      }
    };
  }

  protected RepromiseFunction<String, Void> testStatus(final String host, final int port, final String path, final int expected) {
    return new RepromiseFunction<String, Void>() {
      @Override
      public Promise<Void> call(String deployID) {
        return getUrl(host, port, path)
          .then(new PromiseAction<HttpClientResponse>() {
            @Override
            public void call(HttpClientResponse response) {
              VertxAssert.assertEquals(expected, response.statusCode());
            }
          });
      }
    };
  }

  protected PromiseAction<Exception> onFailure() {
    return new PromiseAction<Exception>() {
      @Override
      public void call(Exception reason) {
        VertxAssert.fail(reason.toString());
      }
    };
  }

  protected FinallyAction onComplete() {
    return new FinallyAction() {
      @Override
      public void call() {
        VertxAssert.testComplete();
      }
    };
  }

  private Promise<HttpClientResponse> getUrl(String host, int port, String path) {
    final Promise<HttpClientResponse> promise = Promise.defer();
    HttpClientRequest request = vertx.createHttpClient()
      .setHost(host)
      .setPort(port)
      .get(path, new Handler<HttpClientResponse>() {
        @Override
        public void handle(HttpClientResponse event) {
          promise.fulfill(event);
        }
      }).putHeader("Cookie", cookies);

    for (String cookie : cookies) {
      System.out.println(cookie);
    }
    
    request.end();
    return promise;
  }

  private Promise<String> getPageContents(HttpClientResponse response) {
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