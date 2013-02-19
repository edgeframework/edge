package org.edgeframework.routing.handler;

import org.edgeframework.routing.HttpServerRequest;
import org.edgeframework.routing.HttpServerResponse;

public abstract class ParamHandler<T> extends Handler {

  public String paramName;

  public void _handle(HttpServerRequest request, HttpServerResponse response) throws Exception {
    this.handle(request, response, (T) request.getParams().get(paramName));
  }

  public abstract void handle(HttpServerRequest request, HttpServerResponse response, T value) throws Exception;
}
