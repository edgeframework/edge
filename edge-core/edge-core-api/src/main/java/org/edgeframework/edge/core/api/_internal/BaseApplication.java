package org.edgeframework.edge.core.api._internal;

import java.util.LinkedList;
import java.util.List;

import org.edgeframework.edge.core.api.Application;
import org.edgeframework.edge.core.api.VertxContainer;
import org.edgeframework.edge.core.api.delegates.Delegate;
import org.edgeframework.edge.core.api.filters.Filter;
import org.vertx.java.core.Future;

public abstract class BaseApplication<V, A extends BaseApplication<V, A>> implements Application<A> {
  // TODO:
  private final List<Delegate<A>> delegates;

  public List<Delegate<A>> getDelegates() {
    return this.delegates;
  }

  public List<Delegate<A>> delegates() {
    return this.delegates;
  }

  private final List<Filter> filters;

  public List<Filter> getFilters() {
    return this.filters;
  }

  public List<Filter> filters() {
    return this.filters;
  }

  private int port = 8080;

  @Override
  public int getPort() {
    return this.port;
  }

  @Override
  public int port() {
    return this.port;
  }

  @Override
  public A setPort(int port) {
    this.port = port;
    return (A) this;
  }

  @Override
  public A port(int port) {
    this.port = port;
    return (A) this;
  }

  private String host = "localhost";

  @Override
  public String getHost() {
    return this.host;
  }

  @Override
  public String host() {
    return this.host;
  }

  @Override
  public A setHost(String host) {
    this.host = host;
    return (A) this;
  }

  @Override
  public A host(String host) {
    this.host = host;
    return (A) this;
  }

  private final V verticle;

  public V getVerticle() {
    return this.verticle;
  }

  public V verticle() {
    return this.verticle;
  }

  public BaseApplication(V verticle) {
    this.verticle = verticle;

    this.delegates = new LinkedList<>();
    this.filters = new LinkedList<>();
  }

  /* Verticle Methods */
  public void start(Future<Void> startedResult) {
    try {
      /* Lifecycle */
      this.beforeStart();

      /* Begin */
      startServer(this.verticle, this.port, this.host);
      startedResult.setResult(null);
      this.afterStart();
    } catch (Throwable e) {
      this.onError(e);
      startedResult.setFailure(e);
    }
  }

  public void stop() {
    try {
      this.beforeStop();
    } catch (Throwable e) {
      this.onError(e);
    }
  }

  /* Delegate Methods */
  private void afterStart() {
    for (Delegate<A> delegate : delegates) {
      delegate.afterStart((A) this);
    }
  }

  private void beforeStart() {
    for (Delegate<A> delegate : delegates) {
      delegate.beforeStart((A) this);
    }
  }

  private void beforeStop() {
    for (Delegate<A> delegate : delegates) {
      delegate.beforeStop((A) this);
    }
  }

  private void onError(Throwable e) {
    for (Delegate<A> delegate : delegates) {
      delegate.onError(e);
    }
  }

  /* Abstract Methods */
  protected abstract void startServer(V verticle, int port, String host);
}
