package org.edgeframework.edge.core.java.http._internal;

import java.util.Iterator;

import org.edgeframework.edge.core.java.filters.Filter;
import org.edgeframework.edge.core.java.filters.FilterContainer;
import org.edgeframework.edge.core.java.http.HttpContext;
import org.edgeframework.edge.core.java.http.HttpRequest;
import org.edgeframework.edge.core.java.http.HttpResponse;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.http.HttpServerRequest;

public class DefaultHttpContext implements HttpContext {
  private Vertx vertx;

  public Vertx getVertx() {
    return this.vertx;
  }

  public Vertx vertx() {
    return this.vertx;
  }

  private HttpRequest request;

  public HttpRequest getRequest() {
    return this.request;
  }

  public HttpRequest request() {
    return this.request;
  }

  private HttpResponse response;

  public HttpResponse getResponse() {
    return this.response;
  }

  public HttpResponse response() {
    return this.response;
  }

  private Iterator<Filter> filterIterator;

  public DefaultHttpContext(Vertx vertx, HttpServerRequest request, FilterContainer filters) {
    this.vertx = vertx;
    this.request = new DefaultHttpRequest(request);
    this.response = new DefaultHttpResponse(request.response());

    this.filterIterator = filters.iterator();
  }

  public void next() {
    if (filterIterator.hasNext()) {
      processFilter(this.vertx, filterIterator.next());
    }
  }

  private void processFilter(final Vertx vertx, final Filter filter) {
    final HttpContext that = this;
    vertx.runOnContext(new Handler<Void>() {
      @Override
      public void handle(Void event) {
        filter.action(that);
      }
    });
  }
}
