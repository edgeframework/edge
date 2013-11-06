package org.edgeframework.edge.core.api._internal;

import java.util.LinkedList;
import java.util.List;

import org.edgeframework.edge.core.api.Application;
import org.edgeframework.edge.core.api.delegates.Delegate;
import org.edgeframework.edge.core.api.delegates.DelegatesContainer;
import org.edgeframework.edge.core.api.filters.Filter;
import org.vertx.java.core.Future;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.http.HttpServer;

public class BaseApplication<A extends BaseApplication<A>> implements Application {
  // TODO:
  private final DelegatesContainer delegates;

  public DelegatesContainer getDelegates() {
    return this.delegates;
  }

  public DelegatesContainer delegates() {
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
  public Application setPort(int port) {
    this.port = port;
    return this;
  }

  @Override
  public Application port(int port) {
    this.port = port;
    return this;
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
  public Application setHost(String host) {
    this.host = host;
    return this;
  }

  @Override
  public Application host(String host) {
    this.host = host;
    return this;
  }

  private Vertx vertx;

  public Vertx getVertx() {
    return this.vertx;
  }

  public Vertx vertx() {
    return this.vertx;
  }

  public BaseApplication(Vertx vertx) {
    this.vertx = vertx;

    this.delegates = null;
    this.filters = new LinkedList<>();
  }

  /* Verticle Methods */
  public void start(Future<Void> startedResult) {
    try {
      /* Lifecycle */
      this.beforeStart();

      /* Begin */
      startServer(this.vertx, this.port, this.host);
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
    for (Delegate delegate : delegates) {
      delegate.afterStart(this);
    }
  }

  private void beforeStart() {
    for (Delegate delegate : delegates) {
      delegate.beforeStart(this);
    }
  }

  private void beforeStop() {
    for (Delegate delegate : delegates) {
      delegate.beforeStop(this);
    }
  }

  private void onError(Throwable e) {
    for (Delegate delegate : delegates) {
      delegate.onError(e);
    }
  }

  /* Abstract Methods */
  protected void startServer(Vertx vertx, int port, String host) {
    HttpServer server = vertx.createHttpServer();

    server.listen(port, host);
  }
}
