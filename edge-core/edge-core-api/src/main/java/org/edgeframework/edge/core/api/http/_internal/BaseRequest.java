package org.edgeframework.edge.core.api.http._internal;

import java.util.Map;

import org.edgeframework.edge.core.api.http.Request;
import org.edgeframework.edge.core.api.http.Response;

public abstract class BaseRequest<R> implements Request {
  private final R vRequest;

  public R vRequest() {
    return this.vRequest;
  }

  private Response response;

  public Response response() {
    return this.response;
  }

  public BaseRequest(R vRequest, Response response) {
    this.vRequest = vRequest;
    this.response = response;
  }

  public Map<String, String> params() {
    throw new UnsupportedOperationException();
  }

}
