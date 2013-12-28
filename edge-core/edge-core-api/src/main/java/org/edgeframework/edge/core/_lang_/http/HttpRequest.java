package org.edgeframework.edge.core._lang_.http;

import org.vertx.java.core.http.HttpServerRequest;

public class HttpRequest {
  private HttpServerRequest request;

  public HttpRequest(HttpServerRequest request) {
    this.request = request;
  }

  public String getPath() {
    return this.request.path();
  }

  public String getMethod() {
    return request.method();
  }
}
