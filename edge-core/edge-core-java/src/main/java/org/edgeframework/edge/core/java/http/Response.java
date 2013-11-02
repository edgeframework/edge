package org.edgeframework.edge.core.java.http;

import org.edgeframework.edge.core.api.http._internal.BaseResponse;
import org.vertx.java.core.http.HttpServerResponse;

public class Response extends BaseResponse<HttpServerResponse> {
  protected Response(HttpServerResponse response) {
    super(response);
  }

  public Response statusCode(int code) {
    this.vResponse().setStatusCode(code);
    return this;
  }

  public Response header(String key, Object value) {
    return this.header(key, value.toString());
  }

  public Response header(String key, String value) {
    this.vResponse().headers().add(key, value);
    return this;
  }

  public Response write(String content) {
    this.vResponse().write(content);
    return this;
  }

  public Response close() {
    this.vResponse().end();
    this.vResponse().close();
    return this;
  }

}
