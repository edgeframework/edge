package org.edgeframework.edge.core.api;

import org.vertx.java.core.Vertx;

public class _DefaultApplication<C> {
  private C delegates;

  public C getDelegates() {
    return this.delegates;
  }

  private Vertx vertx;

  public _DefaultApplication(Vertx vertx, C delegates) {
    this.vertx = vertx;

    this.delegates = delegates;
  }
}
