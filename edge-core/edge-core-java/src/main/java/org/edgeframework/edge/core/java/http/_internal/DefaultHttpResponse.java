package org.edgeframework.edge.core.java.http._internal;

import org.edgeframework.edge.core.java.http.HttpResponse;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpServerResponse;

public class DefaultHttpResponse implements HttpResponse {
  private HttpServerResponse vResponse;

  private Buffer buffer;

  public DefaultHttpResponse(HttpServerResponse vResponse) {
    this.vResponse = vResponse;
    this.buffer = new Buffer();
  }

  public DefaultHttpResponse statusCode(int code) {
    this.vResponse.setStatusCode(code);
    return this;
  }

  public DefaultHttpResponse header(String key, Object value) {
    return this.header(key, value.toString());
  }

  public DefaultHttpResponse header(String key, String value) {
    this.vResponse.headers().add(key, value);
    return this;
  }

  public DefaultHttpResponse write(String content) {
    this.buffer.appendString(content);
    return this;
  }

  public DefaultHttpResponse close() {
    this.vResponse.headers().set("Content-Length", this.buffer.length() + "");
    this.vResponse.write(this.buffer);

    this.vResponse.end();
    this.vResponse.close();
    return this;
  }
}
