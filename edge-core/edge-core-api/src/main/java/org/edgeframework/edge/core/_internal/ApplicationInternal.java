package org.edgeframework.edge.core._internal;

import org.edgeframework.edge.core._internal.filters.FilterContainer;
import org.edgeframework.edge.core._internal.http.HttpContextInternal;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.http.HttpServerRequest;

public interface ApplicationInternal {
  public void handle(Vertx vertx, HttpServerRequest request, FilterContainer filters);
}
