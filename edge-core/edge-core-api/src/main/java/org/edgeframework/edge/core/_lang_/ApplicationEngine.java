package org.edgeframework.edge.core._lang_;

import org.vertx.java.core.Future;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;

public class ApplicationEngine {
  private Vertx vertx;

  public Vertx getVertx() {
    return this.vertx;
  }

  private Application app;

  private int port = 8080;
  private String host = "localhost";

  public ApplicationEngine(Vertx vertx) {
    this.vertx = vertx;
    this.app = app;
  }

  /* Verticle Methods */
  public void start(Future<Void> startedResult) {
    try {
      /* Lifecycle */
      this.app.beforeStart();

      /* Begin */
      startServer(this.vertx, this.port, this.host);
      startedResult.setResult(null);
      this.app.afterStart();
    } catch (Throwable e) {
      this.app.onError(e);
      startedResult.setFailure(e);
    }
  }

  public void stop() {
    try {
      this.app.beforeStop();
    } catch (Throwable e) {
      this.app.onError(e);
    }
  }

  /* Server Methods */
  private void startServer(Vertx vertx, int port, String host) {
    final ApplicationEngine that = this;
    final HttpServer server = vertx.createHttpServer();

    server.requestHandler(new Handler<HttpServerRequest>() {
      @Override
      public void handle(HttpServerRequest request) {
        that.app.handle(request);
      }
    });

    server.listen(port, host);
  }
}
