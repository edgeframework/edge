package org.edgeframework.edge.core.groovy._impl

import groovy.transform.CompileStatic

import org.edgeframework.edge.core._internal.filters.FilterContainer
import org.edgeframework.edge.core.groovy.Application
import org.edgeframework.edge.core.groovy.delegates.AppDelegateContainer
import org.edgeframework.edge.core.groovy.http.HttpContext
import org.vertx.java.core.Vertx
import org.vertx.java.core.http.HttpServerRequest

@CompileStatic
public class DefaultApplication implements Application {
  private DefaultApplicationInternal _internal

  final AppDelegateContainer delegates

  public DefaultApplication(Vertx vertx) {
    _internal = new DefaultApplicationInternal(vertx)
  }

  public AppDelegateContainer getDelegates() {
    return new AppDelegateContainer(this.delegates)
  }

  @Override
  public void handle(Vertx vertx, HttpServerRequest request, FilterContainer filters) {
    def context = new HttpContext(vertx, request, filters)
  }
}
