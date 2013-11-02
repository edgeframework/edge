package org.edgeframework.edge.core.groovy.http

import org.edgeframework.edge.core.api.http._internal.BaseResponse
import org.vertx.groovy.core.http.HttpServerResponse

public class Response extends BaseResponse<HttpServerResponse> {
  protected Response(HttpServerResponse response) {
    super(response)
  }

  @Override
  public Response statusCode(int code) {
    this.vResponse.setStatusCode(code)
    return this
  }

  @Override
  public Response header(String key, Object value) {
    return this.header(key, value.toString())
  }

  @Override
  public Response header(String key, String value) {
    this.vResponse.headers.key = value
    return this
  }

  @Override
  public Response write(String content) {
    this.vResponse.write(content)
    return this
  }

  @Override
  public Response close() {
    this.vResponse.end()
    this.vResponse.close()
    return null
  }
}
