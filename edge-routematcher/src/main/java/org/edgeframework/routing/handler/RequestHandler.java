package org.edgeframework.routing.handler;

import org.edgeframework.routing.HttpServerRequest;
import org.edgeframework.routing.HttpServerResponse;

public abstract class RequestHandler extends Handler {

  public void _handle(HttpServerRequest request, HttpServerResponse response) throws Exception {
    this.handle(request, response);
  }

  public abstract void handle(HttpServerRequest request, HttpServerResponse response) throws Exception;
}
