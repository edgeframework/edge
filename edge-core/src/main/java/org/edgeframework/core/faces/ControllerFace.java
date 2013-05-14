package org.edgeframework.core.faces;

import org.vertx.java.core.Handler;

import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.HttpServerResponse;

public abstract class ControllerFace extends AbstractFace {
  public ControllerFace(String name, String host, int port) {
    super(name, host, port);
  }

  @Override
  void configureServer(HttpServer server) {
    server.requestHandler(new Handler<HttpServerRequest>() {
      @Override
      public void handle(HttpServerRequest event) {

        String path = event.path();
        event.query();

        Result result = ok("index");
        result.render(event.response());
      }
    });
    
  }

  public Result ok(final String content) {
    return new Result() {
      @Override
      public void render(HttpServerResponse response) {
        response.setStatusCode(200);
        response.headers().add("content-length", "" + content.length());
        response.write(content);
        response.close();
      }
    };
  }

  private abstract class Result {
    public abstract void render(HttpServerResponse response);
  }

}
