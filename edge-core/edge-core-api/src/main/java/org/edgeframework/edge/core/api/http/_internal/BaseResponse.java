package org.edgeframework.edge.core.api.http._internal;

import org.edgeframework.edge.core.api.http.Response;

public abstract class BaseResponse<R1> implements Response {
  private final R1 vResponse;

  public R1 vResponse() {
    return this.vResponse;
  }

  public BaseResponse(R1 vResponse) {
    this.vResponse = vResponse;
  }
}
