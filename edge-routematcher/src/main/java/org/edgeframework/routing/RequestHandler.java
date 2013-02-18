package org.edgeframework.routing;

import org.vertx.java.deploy.impl.VertxLocator;

public abstract class RequestHandler {

  private HandlerContext container;

  public RequestHandler() {
  }

  /**
   * Abandons the current hierarchy of requests. All parameters and request data
   * is discarded, and the next route handler (if any) will handle the request.
   */
  protected void next() {
    if (this.container == null) {
      VertxLocator.container.getLogger().warn("Container for EdgeRequest not set... did you call handleRequest by mistake?");
      return;
    }

    this.container.next();
  }

  void _handle(HandlerContext container, HttpServerRequest request, HttpServerResponse response) {
    this.container = container;

    try {
      this.handle(request, response);
    } catch (Throwable e) {
      VertxLocator.container.getLogger().error("Error", e);
      container.exception(e);
    }
    this.container = null;
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
