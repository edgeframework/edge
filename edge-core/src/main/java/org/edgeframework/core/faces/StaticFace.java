package org.edgeframework.core.faces;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.HttpServerResponse;

import com.darylteo.rx.promises.Promise;
import com.darylteo.rx.promises.PromiseAction;

public abstract class StaticFace extends AbstractFace {
  private Path basePath = Paths.get("");

  public StaticFace(String name, String host, int port) {
    super(name, host, port);
  }

  @Override
  void configureServer(HttpServer server) {
    server.requestHandler(new Handler<HttpServerRequest>() {
      @Override
      public void handle(HttpServerRequest event) {
        String requested = event.path();
        if (requested.contains("..")) {
          event.response().setStatusCode(500);
          event.response().setStatusMessage("Invalid path");
          event.response().end();
          return;
        }

        requested = requested.substring(1);
        requested = basePath.resolve(requested).toString();
        container.logger().debug("Requested a file: " + requested);

        checkFileExists(requested)
          .then(new SendFileAction(event.response(), requested));
      }
    });
  }

  private Promise<Boolean> checkFileExists(String path) {
    final Promise<Boolean> promise = Promise.defer();

    vertx.fileSystem().exists(path, new Handler<AsyncResult<Boolean>>() {
      @Override
      public void handle(AsyncResult<Boolean> event) {
        if (event.succeeded()) {
          promise.fulfill(event.result());
        } else {
          promise.fail(event.cause());
        }
      }
    });

    return promise;
  }

  private class SendFileAction extends PromiseAction<Boolean> {
    private HttpServerResponse response;
    private String path;

    public SendFileAction(HttpServerResponse response, String path) {
      this.response = response;
      this.path = path;
    }

    @Override
    public void call(Boolean exists) {
      if (exists) {
        response.sendFile(path);
      } else {
        // TODO: 404
        response.setStatusCode(404);
        response.end();
      }
    }
  }

  public void setBasePath(String basePath) {
    this.basePath = Paths.get(basePath);
  }

  public Path getBasePath() {
    return this.basePath;
  }

}
