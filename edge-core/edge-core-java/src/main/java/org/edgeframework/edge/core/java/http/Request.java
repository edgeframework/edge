package org.edgeframework.edge.core.java.http;

import org.edgeframework.edge.core.api.http._internal.BaseRequest;
import org.vertx.java.core.http.HttpServerRequest;

public class Request extends BaseRequest<HttpServerRequest> {
  private HttpServerRequest vRequest;

  public Request(HttpServerRequest vRequest) {
    super(vRequest, new Response(vRequest.response()));
  }

  public String path() {
    return this.vRequest.path();
  }
}
