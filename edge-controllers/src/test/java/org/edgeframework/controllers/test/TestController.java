package org.edgeframework.controllers.test;

import org.edgeframework.controllers.Controller;
import org.edgeframework.controllers.Result;
import org.edgeframework.promises.Promise;
import org.edgeframework.promises.PromiseHandler;
import org.vertx.java.core.Handler;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.deploy.impl.VertxLocator;

public class TestController extends Controller {

  public static Result testOkResult() {
    return ok("Hello World");
  }

  public static Result testRenderResult() {
    return render("basic", new JsonObject().putString("echo", "Hello World"));
  }

  public static Result testJsonResult() {
    return json(new JsonObject().putString("echo", "Hello World"));
  }

  public static Result testAsyncResult() {
    final Promise<String> promise = Promise.defer();
    VertxLocator.vertx.runOnLoop(new Handler<Void>() {
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

  public static Result testRouteParams(String echoString) {
    return ok(echoString);
  }
}
