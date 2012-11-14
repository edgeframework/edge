package com.darylteo.edge.core.requests;

public abstract class EdgeHandler {

  private EdgeHandlerContainer container;

  public EdgeHandler() {

  }

  EdgeHandler(EdgeHandlerContainer container) {
    this.container = container;
  }

  protected void next() {
    System.out.println("Next");
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
