package org.edgeframework.core.faces;

import rx.util.functions.Func1;

import com.jetdrone.vertx.yoke.middleware.YokeRequest;
import com.jetdrone.vertx.yoke.middleware.YokeResponse;

public abstract class Controller {
  private YokeRequest request;

  void setRequest(YokeRequest request) {
    this.request = request;
  }

  protected abstract class Result {
    public abstract void render(YokeResponse response);
  }

  protected interface TypeConverter extends Func1<String, Object> {
  }

  /* Properties */
  protected YokeRequest request() {
    return this.request;
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
