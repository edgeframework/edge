package org.edgeframework.edge.core._lang_.filters.assets;

import java.nio.file.Paths;

import org.edgeframework.edge.core._lang_.http.Filter;
import org.edgeframework.edge.core._lang_.http.HttpContext;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;

public class Assets implements Filter {
  private String path;

  public Assets(String path) {
    this.path = path;
  }

  @Override
  public void call(final HttpContext context) {
    final String file = Paths.get(path, context.getRequest().getPath()).toString();

    context.getVertx().fileSystem().exists(file, new Handler<AsyncResult<Boolean>>() {
      @Override
      public void handle(AsyncResult<Boolean> event) {
        if (event.succeeded() && event.result()) {
          context.getResponse().sendFile(file);
          context.end();
        } else {
          context.next();
        }
      }
    });
  }
}
