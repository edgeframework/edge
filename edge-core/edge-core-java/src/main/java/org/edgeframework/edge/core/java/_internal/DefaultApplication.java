package org.edgeframework.edge.core.java._internal;

import org.edgeframework.edge.core.java.Application;
import org.edgeframework.edge.core.java.delegates.AppDelegate;
import org.edgeframework.edge.core.java.delegates.AppDelegateContainer;
import org.edgeframework.edge.core.java.delegates._internal.DefaultAppDelegateContainer;
import org.edgeframework.edge.core.java.filters.FilterContainer;
import org.edgeframework.edge.core.java.filters._internal.DefaultFilterContainer;
import org.edgeframework.edge.core.java.http._internal.DefaultHttpContext;
import org.vertx.java.core.Future;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;

public class DefaultApplication implements Application {
  // TODO:
  private final AppDelegateContainer delegates;

  @Override
  public AppDelegateContainer getDelegates() {
    return this.delegates;
  }

  @Override
  public AppDelegateContainer delegates() {
    return this.delegates;
  }

  private final FilterContainer filters;

  @Override
  public FilterContainer getFilters() {
    return this.filters;
  }

  @Override
  public FilterContainer filters() {
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

  public DefaultApplication(Vertx vertx) {
    this.vertx = vertx;

    this.delegates = new DefaultAppDelegateContainer();
    this.filters = new DefaultFilterContainer();
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
    for (AppDelegate delegate : delegates) {
      delegate.afterStart(this);
    }
  }

  private void beforeStart() {
    for (AppDelegate delegate : delegates) {
      delegate.beforeStart(this);
    }
  }

  private void beforeStop() {
    for (AppDelegate delegate : delegates) {
      delegate.beforeStop(this);
    }
  }

  private void onError(Throwable e) {
    for (AppDelegate delegate : delegates) {
      delegate.onError(this, e);
    }
  }

  /* Abstract Methods */
  protected void startServer(Vertx vertx, int port, String host) {
    final DefaultApplication that = this;
    final HttpServer server = vertx.createHttpServer();

    server.requestHandler(new Handler<HttpServerRequest>() {
      @Override
      public void handle(HttpServerRequest request) {
        DefaultHttpContext context = new DefaultHttpContext(that.vertx, request, that.filters);
        context.next();
      }
    });

    server.listen(port, host);
  }
}
