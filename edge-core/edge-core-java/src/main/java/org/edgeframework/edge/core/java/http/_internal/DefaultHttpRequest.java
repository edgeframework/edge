package org.edgeframework.edge.core.java.http._internal;

import org.edgeframework.edge.core.java.http.HttpRequest;
import org.vertx.java.core.http.HttpServerRequest;

public class DefaultHttpRequest implements HttpRequest {
  private HttpServerRequest vRequest;

  public DefaultHttpRequest(HttpServerRequest vRequest) {
    this.vRequest = vRequest;
  }

  public String path() {
    return this.vRequest.path();
  }
}
