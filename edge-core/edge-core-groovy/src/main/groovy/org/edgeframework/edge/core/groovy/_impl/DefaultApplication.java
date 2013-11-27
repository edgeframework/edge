package org.edgeframework.edge.core.groovy._impl;

import org.edgeframework.edge.core._internal.ApplicationInternal;
import org.edgeframework.edge.core._internal._impl.ApplicationEngine;
import org.edgeframework.edge.core.groovy.Application;
import org.edgeframework.edge.core.groovy.delegates.AppDelegate;
import org.edgeframework.edge.core.groovy.delegates.AppDelegateContainer;
import org.edgeframework.edge.core.groovy.delegates._impl.DefaultAppDelegateContainer;
import org.edgeframework.edge.core.groovy.filters.FilterContainer;
import org.edgeframework.edge.core.groovy.filters._impl.DefaultFilterContainer;
import org.edgeframework.edge.core.groovy.http.HttpContext;
import org.vertx.groovy.core.Vertx;
import org.vertx.java.core.Future;
import org.vertx.java.core.http.HttpServerRequest;

public class DefaultApplication implements Application, ApplicationInternal {
  private DefaultAppDelegateContainer delegates;
  private DefaultFilterContainer filters;

  private ApplicationEngine engine;

  private Vertx vertx;

  public DefaultApplication(Vertx vertx) {
    this.vertx = vertx;
    this.engine = new ApplicationEngine(vertx.toJavaVertx(), this);

    this.delegates = new DefaultAppDelegateContainer(this.engine.getDelegates());
    this.filters = new DefaultFilterContainer(this.engine.getFilters());
  }

  @Override
  public AppDelegateContainer getDelegates() {
    return this.delegates;
  }

  @Override
  public FilterContainer getFilters() {
    return this.filters;
  }

  @Override
  public void afterStart() {
    for (AppDelegate delegate : delegates) {
      delegate.afterStart(this);
    }
  }

  @Override
  public void beforeStart() {
    for (AppDelegate delegate : delegates) {
      delegate.beforeStart(this);
    }
  }

  @Override
  public void beforeStop() {
    for (AppDelegate delegate : delegates) {
      delegate.beforeStop(this);
    }
  }

  @Override
  public void onError(Throwable e) {
    for (AppDelegate delegate : delegates) {
      delegate.onError(this, e);
    }
  }

  @Override
  public void handle(HttpServerRequest request) {
    HttpContext context = new HttpContext(this.vertx, request, this.engine.filters);
    context.next();
  }

  @Override
  public void start(Future<Void> startedResult) {
    this.engine.start(startedResult)
  }

  @Override
  public void stop() {
    this.engine.stop()
  }
}
