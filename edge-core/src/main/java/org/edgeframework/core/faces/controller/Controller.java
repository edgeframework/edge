package org.edgeframework.core.faces.controller;

import java.util.Map;

import org.vertx.java.core.Vertx;

import com.jetdrone.vertx.yoke.middleware.YokeRequest;
import com.jetdrone.vertx.yoke.middleware.YokeResponse;

public abstract class Controller {
  private Vertx vertx;
  private YokeRequest request;

  void setRequest(YokeRequest request) {
    this.request = request;
  }

  void setVertx(Vertx vertx) {
    this.vertx = vertx;
  }

  /* Util Classes */
  protected abstract class Result {
    public abstract void render(YokeResponse response);
  }

  /* Properties */
  protected YokeRequest request() {
    return this.request;
  }

  protected Map<String, Object> session() {
    return vertx.sharedData().getMap("session");
  }

  /* Result Methods */
  protected Result ok(final String content) {
    return new Result() {
      @Override
      public void render(YokeResponse response) {
        response.setStatusCode(200);
        response.headers().add("content-length", "" + content.length());
        response.write(content);
        response.close();
      }
    };
  }
}
