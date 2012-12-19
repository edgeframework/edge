package org.edgeframework.routing.handler;

import org.vertx.java.deploy.impl.VertxLocator;

public abstract class EdgeHandler {

  private HandlerChain container;

  public EdgeHandler() {
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

  void handle(HandlerChain container, EdgeRequest request, EdgeResponse response) {
    this.container = container;

    try {
      this.handleRequest(request, response);
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
  public abstract void handleRequest(EdgeRequest request, EdgeResponse response) throws Exception;
}
