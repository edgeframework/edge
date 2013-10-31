package org.edgeframework.core.api.http;

import java.util.LinkedList;
import java.util.List;

import org.edgeframework.core.api.Edge;
import org.edgeframework.core.filters.Filter;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;

public class Context {
  private Vertx vertx;
  private Edge edge;
  private Request request;
  private List<Filter> filters;

  public Edge edge() {
    return this.edge;
  }

  public Request request() {
    return this.request;
  }

  public Context(Edge edge, Request request) {
    this.vertx = edge.vertx();
    this.edge = edge;
    this.request = request;
    this.filters = new LinkedList<>(edge.filters());
  }

  public void begin() {
    next();
  }

  public void next() {
    Handler<Void> actionHandler = new Handler<Void>() {
      @Override
      public void handle(Void event) {
        if (!Context.this.filters.isEmpty()) {
          Filter filter = Context.this.filters.remove(0);
          filter.action(Context.this);
        }
      }
    };
    vertx.runOnContext(actionHandler);
  }
}
