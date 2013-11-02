package org.edgeframework.edge.core.java.http;

import java.util.List;

import org.edgeframework.edge.core.api.Edge;
import org.edgeframework.edge.core.api.filters.Filter;
import org.edgeframework.edge.core.api.http._internal.BaseContext;
import org.vertx.java.core.Vertx;

public class Context extends BaseContext<Vertx> {
  public Context(Edge<Vertx> edge, Request request, List<Filter> filters) {
    super(edge.vertx(), request, filters);
  }

  @Override
  public void next() {
    // TODO Auto-generated method stub

  }

}
