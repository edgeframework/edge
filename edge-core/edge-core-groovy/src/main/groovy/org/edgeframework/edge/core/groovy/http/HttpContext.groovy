package org.edgeframework.edge.core.groovy.http

import groovy.transform.CompileStatic

import org.edgeframework.edge.core._internal.filters.FilterContainerInternal
import org.edgeframework.edge.core._internal.http.HttpContextInternal
import org.vertx.groovy.core.Vertx
import org.vertx.java.core.http.HttpServerRequest

@CompileStatic
public class HttpContext implements HttpContextInternal {
  final Vertx vertx
  Vertx vertx() {
    return this.vertx
  }

  final HttpRequest request
  HttpRequest request() {
    return this.request
  }

  final HttpResponse response
  HttpResponse response() {
    return this.response
  }

  HttpContext(org.vertx.java.core.Vertx jVertex, HttpServerRequest jRequest, FilterContainerInternal jfilters) {
    this.vertx = new Vertx(jVertex)
    this.request = new HttpRequest(jRequest)
    this.response = new HttpResponse(jRequest.response())
  }

  @Override
  public void next() {
    // TODO Auto-generated method stub
  }
}