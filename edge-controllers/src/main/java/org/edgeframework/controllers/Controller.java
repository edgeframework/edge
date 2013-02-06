package org.edgeframework.controllers;

import java.util.Map;

import org.edgeframework.promises.Promise;
import org.edgeframework.promises.PromiseHandler;
import org.edgeframework.routing.handler.EdgeResponse;
import org.vertx.java.core.json.JsonObject;

public abstract class Controller {

  public static Result ok(final String content) {
    return new Result() {
      @Override
      protected void perform(EdgeResponse response) {
        response.send(content);
      }
    };
  }

  public static Result render(final String templateName, final Map<String, Object> context) {
    return new Result() {
      @Override
      protected void perform(EdgeResponse response) throws Exception {
        response.render(templateName, context);
      }
    };
  }

  public static Result render(final String templateName, final JsonObject context) {
    return new Result() {
      @Override
      protected void perform(EdgeResponse response) throws Exception {
        response.render(templateName, context);
      }
    };
  }

  public static Result json(final JsonObject object) {
    return new Result() {
      @Override
      protected void perform(EdgeResponse response) throws Exception {
        System.out.println(object.encode());
        response
            .setContentType("application/json")
            .send(object.encode());
      }
    };
  }

  public static Result async(final Promise<Result> promise) {
    return new Result() {
      @Override
      protected void perform(final EdgeResponse response) throws Exception {
        promise.then(new PromiseHandler<Result, Void>() {
          @Override
          public Void handle(Result result) {
            try {
              result.perform(response);
            } catch (Exception e) {
              // TODO: Exception
            }
            return null;
          }
        });
      }
    };
  }
}
