package org.edgeframework.edge.core.groovy.http;

import java.util.Iterator;

import org.edgeframework.edge.core._internal.http.HttpContextInternal;
import org.edgeframework.edge.core.groovy.filters.Filter;
import org.edgeframework.edge.core.groovy.filters.FilterContainer;
import org.vertx.groovy.core.Vertx;
import org.vertx.java.core.http.HttpServerRequest;

public class HttpContext implements HttpContextInternal {
  final Vertx vertx;

  Vertx vertx() {
    return this.vertx;
  }

  final HttpRequest request;

  HttpRequest request() {
    return this.request;
  }

  final HttpResponse response;

  HttpResponse response() {
    return this.response;
  }

  final Iterator<Filter> iterator;

  public HttpContext(Vertx vertx, HttpServerRequest jRequest, FilterContainer filters) {
    this.vertx = vertx;
    this.request = new HttpRequest(jRequest);
    this.response = new HttpResponse(jRequest.response());

    this.iterator = filters.iterator();
  }

  @Override
  public void next() {
    if (iterator.hasNext()) {
      Filter f = iterator.next();
      f.action(this);
    }
  }
}