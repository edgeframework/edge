package org.edgeframework.routing;

import java.util.LinkedList;
import java.util.List;

import org.edgeframework.routing.handler.EdgeHandler;
import org.edgeframework.routing.handler.HandlerChain;
import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.deploy.impl.VertxLocator;

public class Routing implements Handler<HttpServerRequest> {
  private final List<Route> routes;
  private final List<EdgeHandler> middleware;

  public Routing() {
    this.routes = new LinkedList<>();
    this.middleware = new LinkedList<>();
  }

  public void addRoute(String method, String stringPattern, EdgeHandler... handlers) {
    try {
      Route route = new Route(method, stringPattern, handlers);
      this.routes.add(route);
    } catch (Exception e) {
      VertxLocator.container.getLogger().error("Could not create route", e);
    }
  }

  public void addMiddleware(EdgeHandler handler) {
    this.middleware.add(handler);
  }

  @Override
  public void handle(HttpServerRequest request) {
    RouteMatcher matcher = new RouteMatcher(request.method, request.path, this.routes);
    new HandlerChain(request, matcher, this.middleware);
  }
  
}
