package org.edgeframework.edge.core._lang_.components;

import org.edgeframework.edge.core._lang_.http.Filter;
import org.edgeframework.edge.core._lang_.http.HttpContext;

public class Router implements Filter {
  private RouteContainer routes = new RouteContainer();

  public RouteContainer getRoutes() {
    return this.routes;
  }

  @Override
  public void call(HttpContext context) {
    System.out.println("Request received!");
    context.end();
  }

}
