package org.edgeframework.edge.core.groovy.http

import org.edgeframework.edge.core.api.filters.Filter
import org.edgeframework.edge.core.api.http.Request
import org.edgeframework.edge.core.api.http._internal.BaseContext
import org.vertx.groovy.core.Vertx
import org.vertx.java.core.Handler

public class Context extends BaseContext<Vertx> {
  public Context(Vertx vertx, Request request, List<Filter> filters) {
    super(vertx, request, filters)
  }

  @Override
  protected void processFilter(Filter filter) {
    this.vertx().runOnContext { filter.action(this) }
  }
}
