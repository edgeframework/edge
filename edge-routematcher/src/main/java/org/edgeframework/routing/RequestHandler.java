package org.edgeframework.routing;

import org.vertx.java.deploy.impl.VertxLocator;

public abstract class RequestHandler {

  private HandlerContext context;

  public RequestHandler() {
  }

  /**
   * Abandons the current hierarchy of requests. All parameters and request data
   * is discarded, and the next route handler (if any) will handle the request.
   */
  protected void next() {
    if (this.context == null) {
      VertxLocator.container.getLogger().warn("Container for EdgeRequest not set... did you call handleRequest by mistake?");
      return;
    }

    this.context.next();
  }

  void _handle(HandlerContext context, HttpServerRequest request, HttpServerResponse response) {
    this.context = context;

    try {
      this.handle(request, response);
    } catch (Throwable e) {
      VertxLocator.container.getLogger().error("Error", e);
      context.exception(e);
    }
    this.context = null;
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
