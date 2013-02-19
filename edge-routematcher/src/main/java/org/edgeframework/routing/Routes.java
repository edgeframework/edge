package org.edgeframework.routing;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.edgeframework.routing.handler.HandlerContext;
import org.edgeframework.routing.handler.ParamHandler;
import org.edgeframework.routing.handler.RequestHandler;
import org.vertx.java.core.http.HttpServerRequest;

public class Routes {
  private List<RouteDefinition> routes = new LinkedList<>();
  private List<RequestHandler> middleware = new LinkedList<>();
  private Map<String, List<ParamHandler<?>>> params = new HashMap<>();

  public void addRouteDefinition(RouteDefinition route) {
    this.routes.add(route);
  }

  public void addMiddleware(RequestHandler handler) {
    this.middleware.add(handler);
  }

  public void addParamHandler(String name, ParamHandler<?> handler) {
    // TODO: there has to be a more elegant way to solve this ...
    handler.paramName = name;

    if (!this.params.containsKey(name)) {
      this.params.put(name, new LinkedList<ParamHandler<?>>());
    }

    this.params.get(name).add(handler);
  }

  public List<RouteDefinition> getRouteDefinitions() {
    return new LinkedList<>(this.routes);
  }

  public List<RequestHandler> getMiddleware() {
    return new LinkedList<>(this.middleware);
  }

  public List<ParamHandler<?>> getHandlersForParam(String name) {
    if (!this.params.containsKey(name)) {
      return new LinkedList<>();
    }

    return new LinkedList<>(this.params.get(name));
  }

  public HandlerContext getHandlerContext(HttpServerRequest request) {
    return new HandlerContext(request, this);
  }

}
