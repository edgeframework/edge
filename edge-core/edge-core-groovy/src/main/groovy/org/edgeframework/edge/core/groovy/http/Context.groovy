package org.edgeframework.edge.core.groovy.http;

import java.util.List;

import org.edgeframework.edge.core.api.Edge;
import org.edgeframework.edge.core.api.filters.Filter;
import org.edgeframework.edge.core.api.http.Request;
import org.edgeframework.edge.core.api.http._internal.BaseContext;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;

public class Context extends BaseContext<Vertx> {
  public Context(Vertx vertx, Request request, List<Filter> filters) {
    super(vertx, request, filters);
  }

  public void next() {
    // Handler<Void> actionHandler = new Handler<Void>() {
    // @Override
    // public void handle(Void event) {
    // if (!Context.this.filters.isEmpty()) {
    // Filter filter = Context.this.filters.remove(0);
    // filter.action(Context.this);
    // }
    // }
    // };
    // vertx.runOnContext(actionHandler);
  }
}
