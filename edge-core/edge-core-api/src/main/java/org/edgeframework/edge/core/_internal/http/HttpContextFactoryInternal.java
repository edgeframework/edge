package org.edgeframework.edge.core._internal.http;

import org.edgeframework.edge.core._internal.filters.FilterContainerInternal;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.http.HttpServerRequest;

public interface HttpContextFactoryInternal {
  public HttpContextInternal newContext(Vertx vertx, HttpServerRequest request, FilterContainerInternal filters);
}
