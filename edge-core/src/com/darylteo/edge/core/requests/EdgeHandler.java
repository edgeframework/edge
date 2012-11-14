package com.darylteo.edge.core.requests;

import org.vertx.java.deploy.impl.VertxLocator;

public abstract class EdgeHandler {

  private EdgeHandlerContainer container;

  public EdgeHandler() {
  }

  /**
   * Abandons the current hierarchy of requests. All parameters and request data
   * is discarded, and the next route handler (if any) will handle the request.
   */
  protected void next() {
    this.container.next();
  }

  void handle(EdgeHandlerContainer container, EdgeRequest request, EdgeResponse response) {
    if (this.container != null) {
      VertxLocator.container.getLogger().warn("Container for EdgeRequest not set... did you call handleRequest by mistake?");
      return;
    }

    this.container = container;
    this.handleRequest(request, response);
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
  public abstract void handleRequest(EdgeRequest request, EdgeResponse response);
}
