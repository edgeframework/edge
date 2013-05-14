package org.edgeframework.core.faces;

import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServerResponse;

import com.jetdrone.vertx.yoke.Yoke;
import com.jetdrone.vertx.yoke.middleware.Router;
import com.jetdrone.vertx.yoke.middleware.YokeRequest;

public abstract class ControllerFace extends AbstractFace {
  public ControllerFace(String name, String host, int port) {
    super(name, host, port);
  }

  @Override
  void configure(Yoke yoke) {
    Router router = new Router();
    router.get("/index", new Handler<YokeRequest>() {
      @Override
      public void handle(YokeRequest request) {
        String query = request.getParams().get("query");

        query = query == null ? "index" : query;
        request.response().end(query);
      }
    });

    yoke.use(router);
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
