package org.edgeframework.edge.core.java.http;

import java.util.List;

import org.edgeframework.edge.core.api.Edge;
import org.edgeframework.edge.core.api.filters.Filter;
import org.edgeframework.edge.core.api.http._internal.BaseContext;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;

public class Context extends BaseContext<Vertx> {
  public Context(Edge<Vertx> edge, Request request, List<Filter> filters) {
    super(edge.vertx(), request, filters);
  }

  @Override
  protected void processFilter(final Filter filter) {
    this.vertx().runOnContext(new Handler<Void>() {
      @Override
      public void handle(Void event) {
        filter.action(Context.this);
      }
    });
  }
}
