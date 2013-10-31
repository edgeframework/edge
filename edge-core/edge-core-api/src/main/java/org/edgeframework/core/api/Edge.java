package org.edgeframework.core.api;

import java.util.LinkedList;
import java.util.List;

import org.edgeframework.core.filters.Filter;
import org.vertx.java.core.Vertx;
import org.vertx.java.platform.Container;
import org.vertx.java.platform.Verticle;

public abstract class Edge extends Verticle {
  public Vertx vertx() {
    return super.vertx;
  }

  public Container container() {
    return super.container;
  }

  /* Verticle Methods */
  @Override
  public void start(org.vertx.java.core.Future<Void> startedResult) {
    try {
      beforeStart();
      __configure();
      __begin();
      afterStart();

      startedResult.setResult(null);
    } catch (Throwable e) {
      onError(e);
      startedResult.setFailure(e);
    }
  }

  @Override
  public void stop() {
    try {
      beforeStop();
    } catch (Throwable e) {
      onError(e);
    }
  }

  /* Filters */
  private final List<Filter> filters = new LinkedList<>();

  public List<Filter> filters() {
    return this.filters;
  }

  public void filter(Filter filter) {
    this.filters.add(filter);
  }

  /* Lifecycle Hooks */
  public void beforeStart() {
  }

  public void afterStart() {
  }

  public void beforeStop() {
  }

  public void onError(Throwable error) {
  }

  /* Internal Hooks */
  protected abstract void __configure() throws Exception;

  protected abstract void __begin() throws Exception;
}
