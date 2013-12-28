package org.edgeframework.edge.core._lang_.vertx;

public class Vertx implements VertxWrapper<org.vertx.java.core.Vertx> {
  private org.vertx.java.core.Vertx internal;

  @Override
  public org.vertx.java.core.Vertx getInternal() {
    return this.internal;
  }

  private FileSystem fileSystem;

  public FileSystem getFileSystem() {
    return this.fileSystem;
  }

  public Vertx(org.vertx.java.core.Vertx internal) {
    this.internal = internal;

    this.fileSystem = new FileSystem(internal.fileSystem());
  }
}
