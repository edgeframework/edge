package org.edgeframework.routing.handler;

import org.edgeframework.routing.HttpServerRequest;
import org.edgeframework.routing.HttpServerResponse;

abstract class Handler {
  private HandlerContext context;

  protected void next() {
    this.context.next();
  }

  protected void exception(Exception e) {
    this.context.exception(e);
  }

  void _begin(HandlerContext context, HttpServerRequest request, HttpServerResponse response) {
    this.context = context;

    try {
      this._handle(request, response);
    } catch (Exception e) {
      exception(e);
    }
  }

  abstract protected void _handle(HttpServerRequest request, HttpServerResponse response) throws Exception;
}
