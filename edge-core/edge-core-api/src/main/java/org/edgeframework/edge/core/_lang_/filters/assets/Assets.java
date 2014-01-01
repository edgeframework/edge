package org.edgeframework.edge.core._lang_.filters.assets;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.edgeframework.edge.core._lang_.http.Context;
import org.edgeframework.edge.core._lang_.http.Filter;
import org.edgeframework.edge.core._lang_.vertx.Vertx;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;

public class Assets implements Filter {
  private Path path;

  public Assets(String path) {
    this(Paths.get(path));
  }

  public Assets(Path path) {
    this.path = path;
  }

  @Override
  public void call(final Context context) {
    // relativize the requested path based on the root path
    String requested = context.getRequest().getPath();

    if (requested.isEmpty()) {
      context.next();
      return;
    }
    requested = requested.substring(1);

    Path file = this.path.resolve(requested);
    if (file.equals(this.path)) {
      context.next();
      return;
    }

    // deliver the file if it exists
    final String pathString = file.toString();
    final Vertx vertx = context.getServices().get("vertx", Vertx.class);

    vertx.getFileSystem().exists(pathString, new Handler<AsyncResult<Boolean>>() {
      @Override
      public void handle(AsyncResult<Boolean> event) {
        if (event.succeeded() && event.result()) {
          context.getResponse().sendFile(pathString);
          context.end();
        } else {
          context.next();
        }
      }
    });
  }
}
