package org.edgeframework.routing;

import org.vertx.java.deploy.impl.VertxLocator;

public abstract class RequestHandler {

  private HandlerContext context;

  public RequestHandler() {
  }

  protected void next() {
    if (this.context == null) {
      VertxLocator.container.getLogger().warn("Context for Request not set... did you call handle by mistake?");
      return;
    }

    this.context.next();
  }

  void _handle(HandlerContext context, HttpServerRequest request, HttpServerResponse response) {
    this.context = context;

    try {
      // There is a possiblity that this might be asynchronous.
      this.handle(request, response);
    } catch (Exception e) {
      VertxLocator.container.getLogger().error("Error", e);
      this.context.exception(e);
    }
  }

  /**
   * Initiates the handling of the request.
   * 
   * @param request
   *          - the request
   * @param response
   *          - the response
   */
  public abstract void handle(HttpServerRequest request, HttpServerResponse response) throws Exception;
}
