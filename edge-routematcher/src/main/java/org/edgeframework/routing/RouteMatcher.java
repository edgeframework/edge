package org.edgeframework.routing;

import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.deploy.impl.VertxLocator;

public class RouteMatcher implements Handler<HttpServerRequest> {
  public static class HTTP_METHOD {
    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String PUT = "PUT";
    public static final String DELETE = "DELETE";
  }

  private final Routes routes = new Routes();

  public RouteMatcher get(String stringPattern, RequestHandler... handlers) {
    return this.route(HTTP_METHOD.GET, stringPattern, handlers);
  }

  public RouteMatcher post(String stringPattern, RequestHandler... handlers) {
    return this.route(HTTP_METHOD.POST, stringPattern, handlers);
  }

  public RouteMatcher put(String stringPattern, RequestHandler... handlers) {
    return this.route(HTTP_METHOD.PUT, stringPattern, handlers);
  }

  public RouteMatcher delete(String stringPattern, RequestHandler... handlers) {
    return this.route(HTTP_METHOD.DELETE, stringPattern, handlers);
  }

  public RouteMatcher all(String stringPattern, RequestHandler... handlers) {
    this.route(HTTP_METHOD.GET, stringPattern, handlers);
    this.route(HTTP_METHOD.POST, stringPattern, handlers);
    this.route(HTTP_METHOD.PUT, stringPattern, handlers);
    return this.route(HTTP_METHOD.DELETE, stringPattern, handlers);
  }

  public RouteMatcher route(String method, String stringPattern, RequestHandler... handlers) {
    try {
      RouteDefinition route = new RouteDefinition(method, stringPattern, handlers);
      this.routes.addRouteDefinition(route);
    } catch (Exception e) {
      VertxLocator.container.getLogger().error("Could not create route", e);
    }

    return this;
  }

  public RouteMatcher use(RequestHandler handler) {
    this.routes.addMiddleware(handler);
    return this;
  }

  @Override
  public void handle(HttpServerRequest request) {
     this.routes.getHandlerContext(request);
  }

}
