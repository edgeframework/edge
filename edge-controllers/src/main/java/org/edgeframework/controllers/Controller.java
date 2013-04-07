package org.edgeframework.controllers;

import java.util.Map;

import org.edgeframework.routing.HttpServerRequest;
import org.edgeframework.routing.HttpServerResponse;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.json.JsonObject;

import com.darylteo.rx.promises.Promise;
import com.darylteo.rx.promises.PromiseAction;

public abstract class Controller {

  private Vertx vertx;
  private HttpServerRequest request;

  // Setters
  void setVertx(Vertx vertx) {
    this.vertx = vertx;
  }

  void setRequest(HttpServerRequest request) {
    this.request = request;
  }

  // Request Getters
  public Vertx vertx() {
    return this.vertx;
  }

  public Map<String, Object> query() {
    return this.request.getQuery();
  }

  @SuppressWarnings("unchecked")
  public <T> T query(String name) {
    return (T) query().get(name);
  }

  public Map<String, Object> data() {
    return this.request.getData();
  }

  @SuppressWarnings("unchecked")
  public <T> T data(String name) {
    return (T) data().get(name);
  }

  public Map<String, Object> body() {
    return this.request.getBody();
  }

  @SuppressWarnings("unchecked")
  public <T> T body(String name) {
    return (T) body().get("name");
  }

  /* Result Factory Methods */
  public static Result ok(final String content) {
    return new Result() {
      @Override
      protected void perform(HttpServerResponse response) {
        response.send(content);
      }
    };
  }

  public static Result render(final String templateName, final Map<String, Object> context) {
    return new Result() {
      @Override
      protected void perform(HttpServerResponse response) throws Exception {
        response.render(templateName, context);
      }
    };
  }

  public static Result render(final String templateName, final JsonObject context) {
    return new Result() {
      @Override
      protected void perform(HttpServerResponse response) throws Exception {
        response.render(templateName, context);
      }
    };
  }

  public static Result json(final JsonObject object) {
    return new Result() {
      @Override
      protected void perform(HttpServerResponse response) throws Exception {
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
      protected void perform(final HttpServerResponse response)
          throws Exception {
        promise.then(new PromiseAction<Result>() {
          @Override
          public void call(Result result) {
            try {
              result.perform(response);
            } catch (Exception e) {
              // TODO: Exception
            }
          }
        });
      }
    };
  }
}
