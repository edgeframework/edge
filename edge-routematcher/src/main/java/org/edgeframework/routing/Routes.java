package org.edgeframework.routing;

import java.util.LinkedList;
import java.util.List;

import org.vertx.java.core.http.HttpServerRequest;

public class Routes {
  private List<RouteDefinition> routes = new LinkedList<>();
  private List<RequestHandler> middleware = new LinkedList<>();

  public void addRouteDefinition(RouteDefinition route) {
    this.routes.add(route);
  }

  public void addMiddleware(RequestHandler handler) {
    this.middleware.add(handler);
  }

  public HandlerContext getHandlerContext(HttpServerRequest request) {
    return new HandlerContext(request, this.routes, this.middleware);
  }
}
