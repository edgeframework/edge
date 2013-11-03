package org.edgeframework.edge.core.api.http._internal;

import java.util.LinkedList;
import java.util.List;

import org.edgeframework.edge.core.api.filters.Filter;
import org.edgeframework.edge.core.api.http.Context;
import org.edgeframework.edge.core.api.http.Request;
import org.edgeframework.edge.core.api.http.Response;

public abstract class BaseContext<V> implements Context<V> {
  private V vertx;

  @Override
  public V vertx() {
    return this.vertx;
  }

  private Request request;

  @Override
  public Request request() {
    return this.request;
  }

  @Override
  public Response response() {
    return this.request.response();
  }

  private List<Filter> filters;

  public BaseContext(V vertx, Request request, List<Filter> filters) {
    this.vertx = vertx;
    this.request = request;
    this.filters = new LinkedList<>(filters);
  }

  @Override
  public void next() {
    if (filters.isEmpty()) {
      // TODO: Execute controller
      return;
    }

    this.processFilter(filters.remove(0));
  }

  protected abstract void processFilter(Filter filter);
}
