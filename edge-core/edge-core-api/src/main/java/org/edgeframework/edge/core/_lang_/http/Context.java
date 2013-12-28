package org.edgeframework.edge.core._lang_.http;

import java.util.LinkedList;
import java.util.List;

import org.edgeframework.edge.core._lang_.app.Application;
import org.edgeframework.edge.core._lang_.services.ServicesContainer;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.http.HttpServerRequest;

public class Context {
  private Vertx vertx;

  private Application app;

  public Application getApplication() {
    return this.app;
  }

  public ServicesContainer getServices() {
    return this.app.getServices();
  }

  private HttpRequest request;

  public HttpRequest getRequest() {
    return this.request;
  }

  private HttpResponse response;

  public HttpResponse getResponse() {
    return this.response;
  }

  private List<Filter> filters;

  public Context(Application app, Vertx vertx, HttpServerRequest request, List<Filter> filters) {
    this.vertx = vertx;
    this.app = app;

    this.request = new HttpRequest(request);
    this.response = new HttpResponse(request.response());
    this.filters = new LinkedList<>(filters);

    next();
  }

  public void next() {
    if (!this.filters.isEmpty()) {
      vertx.runOnContext(new Handler<Void>() {
        @Override
        public void handle(Void event) {
          Filter filter = Context.this.filters.remove(0);
          filter.call(Context.this);
        }
      });
    } else {
      end();
    }
  }

  public void end() {
    this.response.close();

    this.filters = null;
    this.request = null;
    this.response = null;
  }
}
