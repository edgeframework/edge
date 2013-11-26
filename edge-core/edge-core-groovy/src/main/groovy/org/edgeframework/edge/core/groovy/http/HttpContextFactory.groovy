package org.edgeframework.edge.core.groovy.http

import groovy.transform.CompileStatic

import org.edgeframework.edge.core._internal.filters.FilterContainerInternal
import org.edgeframework.edge.core._internal.http.HttpContextFactoryInternal
import org.edgeframework.edge.core._internal.http.HttpContextInternal
import org.vertx.java.core.Vertx
import org.vertx.java.core.http.HttpServerRequest

@CompileStatic
class HttpContextFactory implements HttpContextFactoryInternal {

  @Override
  public HttpContextInternal newContext(Vertx vertx, HttpServerRequest request, FilterContainerInternal filters) {
    return new HttpContext(vertx, request, filters)
  }
}
