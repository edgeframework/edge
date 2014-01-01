package org.edgeframework.edge.core._lang_.http;

import org.vertx.java.core.http.HttpServerResponse;

public class HttpResponse {
  private HttpServerResponse response;

  public HttpResponse(HttpServerResponse response) {
    this.response = response;
  }

  public HttpResponse statusCode(int code) {
    this.response.setStatusCode(code);
    return this;
  }

  public HttpResponse header(String key, Object value) {
    this.header(key, value.toString());
    return this;
  }

  public HttpResponse header(String key, String value) {
    this.response.headers().add(key, value);
    return this;
  }

  public HttpResponse write(String content) {
    this.response.write(content);
    return this;
  }

  public HttpResponse sendFile(String file) {
    this.response.sendFile(file);
    return this;
  }

  public HttpResponse close() {
    this.response.close();
    return this;
  }
}
