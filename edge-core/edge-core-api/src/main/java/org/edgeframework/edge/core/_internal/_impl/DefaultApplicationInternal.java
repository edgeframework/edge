package org.edgeframework.edge.core._internal._impl;

import org.edgeframework.edge.core._internal.ApplicationInternal;
import org.edgeframework.edge.core._internal.delegates.AppDelegateContainerInternal;
import org.edgeframework.edge.core._internal.delegates.AppDelegateInternal;
import org.edgeframework.edge.core._internal.filters.FilterContainer;
import org.edgeframework.edge.core._internal.http.HttpContextFactory;
import org.vertx.java.core.Future;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;

public abstract class DefaultApplicationInternal implements ApplicationInternal {
  private AppDelegateContainerInternal delegates;
  private FilterContainer filters;

  private int port = 8080;
  private String host = "localhost";

  private Vertx vertx;

  public DefaultApplicationInternal(Vertx vertx, AppDelegateContainerInternal delegates, FilterContainer filters, HttpContextFactory factory) {
    this.vertx = vertx;

    this.delegates = delegates;
    this.filters = filters;
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
    for (AppDelegateInternal delegate : delegates) {
      delegate.afterStart(this);
    }
  }

  private void beforeStart() {
    for (AppDelegateInternal delegate : delegates) {
      delegate.beforeStart(this);
    }
  }

  private void beforeStop() {
    for (AppDelegateInternal delegate : delegates) {
      delegate.beforeStop(this);
    }
  }

  private void onError(Throwable e) {
    for (AppDelegateInternal delegate : delegates) {
      delegate.onError(this, e);
    }
  }

  /* Abstract Methods */
  protected void startServer(Vertx vertx, int port, String host) {
    final DefaultApplicationInternal that = this;
    final HttpServer server = vertx.createHttpServer();

    server.requestHandler(new Handler<HttpServerRequest>() {
      @Override
      public void handle(HttpServerRequest request) {
        that.handle(that.vertx, request, that.filters);
      }
    });

    server.listen(port, host);
  }
}
