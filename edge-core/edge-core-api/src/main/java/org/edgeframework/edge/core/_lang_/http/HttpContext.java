package org.edgeframework.edge.core._lang_.http;

import java.util.LinkedList;
import java.util.List;

import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.http.HttpServerRequest;

public class HttpContext {
  private Vertx vertx;

  private HttpRequest request;

  public HttpRequest getRequest() {
    return this.request;
  }

  private HttpResponse response;

  public HttpResponse getResponse() {
    return this.response;
  }

  private List<Filter> filters;

  public HttpContext(Vertx vertx, HttpServerRequest request, List<Filter> filters) {
    this.vertx = vertx;

    this.request = new HttpRequest(request);
    this.response = new HttpResponse(request.response());
    this.filters = new LinkedList<>(filters);
  }

  public void next() {
    if (!this.filters.isEmpty()) {
      vertx.runOnContext(new Handler<Void>() {
        @Override
        public void handle(Void event) {
          Filter filter = HttpContext.this.filters.remove(0);
          filter.call(HttpContext.this);
        }
      });
    } else {
      this.response.close();
    }
  }

  public void end() {
    this.response.close();
  }
}
