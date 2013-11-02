package org.edgeframework.edge.core.groovy.http

import org.edgeframework.edge.core.api.http._internal.BaseRequest
import org.vertx.groovy.core.http.HttpServerRequest

public class Request extends BaseRequest<HttpServerRequest> {
  public Request(HttpServerRequest vRequest) {
    super(vRequest, new Response(vRequest.response))
  }

  public Map params() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException()
  }

  public String path() {
    return this.vRequest().path()
  }
}
