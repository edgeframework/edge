package org.edgeframework.core.api.http;

import org.vertx.java.core.MultiMap;
import org.vertx.java.core.http.HttpServerRequest;

public class Request {
  private Response response;

  private HttpServerRequest vRequest;

  public Request(HttpServerRequest vRequest) {
    this.vRequest = vRequest;
    this.response = new Response(vRequest.response());
  }

  public MultiMap params() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException();
  }

  public Response response() {
    return this.response;
  }

  public String path() {
    return this.vRequest.path();
  }

}
