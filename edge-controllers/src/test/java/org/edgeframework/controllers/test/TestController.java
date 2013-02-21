package org.edgeframework.controllers.test;

import org.edgeframework.controllers.Controller;
import org.edgeframework.controllers.Result;
import org.edgeframework.promises.Promise;
import org.edgeframework.promises.PromiseHandler;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.json.JsonObject;

public class TestController extends Controller {

  public Result testOkResult() {
    return ok("Hello World");
  }

  public Result testRenderResult() {
    return render("basic", new JsonObject().putString("echo", "Hello World"));
  }

  public Result testJsonResult() {
    return json(new JsonObject().putString("echo", "Hello World"));
  }

  public Result testAsyncResult() {
    final Promise<String> promise = Promise.defer();
    vertx().runOnLoop(new Handler<Void>() {
      @Override
      public void handle(Void arg) {
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }

        promise.fulfill("Hello World");
      }
    });

    Promise<Result> result = promise.then(
        new PromiseHandler<String, Result>() {
          @Override
          public Result handle(String value) {
            return ok(value);
          }
        }

        );

    return async(result);
  }

  public Result testRouteParams(String echoString) {
    return ok(echoString);
  }

}
