package org.edgeframework.edge.core.groovy._impl

import groovy.transform.CompileStatic

import org.edgeframework.edge.core._internal._impl.DefaultApplicationInternal
import org.edgeframework.edge.core._internal.filters.FilterContainerInternal
import org.edgeframework.edge.core.groovy.Application
import org.edgeframework.edge.core.groovy.delegates.AppDelegateContainer
import org.edgeframework.edge.core.groovy.delegates._impl.DefaultAppDelegateContainer
import org.edgeframework.edge.core.groovy.filters._impl.DefaultFilterContainer;
import org.edgeframework.edge.core.groovy.http.HttpContext
import org.edgeframework.edge.core.groovy.http.HttpContextFactory
import org.vertx.java.core.Vertx
import org.vertx.java.core.http.HttpServerRequest

@CompileStatic
public class DefaultApplication implements Application {
  private DefaultApplicationInternal _internal

  private DefaultAppDelegateContainer delegates
  private DefaultFilterContainer filters

  public DefaultApplication(Vertx vertx) {
    delegates = new DefaultAppDelegateContainer()
    filters = new DefaultFilterContainer()

    _internal = new DefaultApplicationInternal(vertx, delegates, filters, new HttpContextFactory())
  }

  public AppDelegateContainer getDelegates() {
    //    return new DefaultAppDelegateContainer(this.delegates)
  }

  @Override
  public void handle(Vertx vertx, HttpServerRequest request, FilterContainerInternal filters) {
    def context = new HttpContext(vertx, request, filters)
  }
}
