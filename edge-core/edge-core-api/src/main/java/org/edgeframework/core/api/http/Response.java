package org.edgeframework.core.api.http;

import org.vertx.java.core.http.HttpServerResponse;

public class Response {
  private HttpServerResponse vResponse;

  protected Response(HttpServerResponse response) {
    this.vResponse = response;
  }

  public Response setStatusCode(int i) {
    // TODO Auto-generated method stub
    return this;
  }

  public Response header(String key, Object value) {
    return this.header(key, value.toString());
  }

  public Response header(String key, String value) {
    this.vResponse.headers().add(key, value);
    return this;
  }

  public Response write(String content) {
    this.vResponse.write(content);
    return this;
  }

  public Response close() {
    this.vResponse.end();
    this.vResponse.close();
    return this;
  }

}
