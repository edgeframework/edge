package org.edgeframework.edge.core._lang_.vertx;

import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;

public class FileSystem implements VertxWrapper<org.vertx.java.core.file.FileSystem> {
  private org.vertx.java.core.file.FileSystem internal;

  @Override
  public org.vertx.java.core.file.FileSystem getInternal() {
    return this.internal;
  }

  public FileSystem(org.vertx.java.core.file.FileSystem internal) {
    this.internal = internal;
  }

  // change to implementation of promises.
  public void exists(String path, Handler<AsyncResult<Boolean>> handler) {
    internal.exists(path, handler);
  }
}
