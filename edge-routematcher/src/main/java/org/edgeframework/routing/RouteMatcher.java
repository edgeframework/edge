package org.edgeframework.routing;

import java.util.LinkedList;
import java.util.List;

import org.edgeframework.routing.handler.EdgeHandler;
import org.edgeframework.routing.handler.HandlerChain;
import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.deploy.impl.VertxLocator;

public class RouteMatcher implements Handler<HttpServerRequest> {
  public static class HTTP_METHOD {
    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String PUT = "PUT";
    public static final String DELETE = "DELETE";
  }

  private final List<Route> routes;
  private final List<EdgeHandler> middleware;

  public RouteMatcher() {
    this.routes = new LinkedList<>();
    this.middleware = new LinkedList<>();
  }

  public RouteMatcher get(String stringPattern, EdgeHandler... handlers) {
    return this.addRoute(HTTP_METHOD.GET, stringPattern, handlers);
  }

  public RouteMatcher post(String stringPattern, EdgeHandler... handlers) {
    return this.addRoute(HTTP_METHOD.POST, stringPattern, handlers);
  }

  public RouteMatcher put(String stringPattern, EdgeHandler... handlers) {
    return this.addRoute(HTTP_METHOD.PUT, stringPattern, handlers);
  }

  public RouteMatcher delete(String stringPattern, EdgeHandler... handlers) {
    return this.addRoute(HTTP_METHOD.DELETE, stringPattern, handlers);
  }

  public RouteMatcher all(String stringPattern, EdgeHandler... handlers) {
    this.addRoute(HTTP_METHOD.GET, stringPattern, handlers);
    this.addRoute(HTTP_METHOD.POST, stringPattern, handlers);
    this.addRoute(HTTP_METHOD.PUT, stringPattern, handlers);
    return this.addRoute(HTTP_METHOD.DELETE, stringPattern, handlers);
  }

  public RouteMatcher addRoute(String method, String stringPattern, EdgeHandler... handlers) {
    try {
      Route route = new Route(method, stringPattern, handlers);
      this.routes.add(route);
    } catch (Exception e) {
      VertxLocator.container.getLogger().error("Could not create route", e);
    }

    return this;
  }

  public RouteMatcher addMiddleware(EdgeHandler handler) {
    this.middleware.add(handler);
    return this;
  }

  @Override
  public void handle(HttpServerRequest request) {
    Matcher matcher = new Matcher(request.method, request.path, this.routes);
    new HandlerChain(request, matcher, this.middleware);
  }

}
