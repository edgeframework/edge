package org.edgeframework.core.faces;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.HttpServerResponse;

import com.darylteo.rx.promises.Promise;
import com.darylteo.rx.promises.PromiseAction;

public abstract class StaticFace extends Face {

  private Path basePath = Paths.get("");
  private String host = "localhost";
  private int port = 8080;

  public StaticFace(String name) {
    super(name);
  }

  @Override
  public void start() {
    super.start();

    vertx.createHttpServer().requestHandler(new Handler<HttpServerRequest>() {
      @Override
      public void handle(HttpServerRequest event) {
        String requested = event.path();
        requested = requested.substring(1);

        if (requested.contains("..")) {
          // 500
          return;
        }

        System.out.println(basePath);
        requested = basePath.resolve(requested).toString();
        System.out.println("Requested a file: " + requested);

        checkFileExists(requested)
            .then(new SendFileAction(event.response(), requested));
      }
    })
        .listen(this.port, this.host);
  }

  @Override
  public void stop() {
    super.stop();
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

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

}
