package com.darylteo.edge.core.routing;

import java.util.HashMap;
import java.util.Map;

import org.vertx.java.deploy.impl.VertxLocator;

import com.darylteo.edge.core.requests.EdgeHandler;

public class Routing {
  private final Map<String, Route> routes;

  public Routing() {
    this.routes = new HashMap<>();
  }

  public void addRoute(String method, String stringPattern, EdgeHandler handler) {
    try {
      Route route = routes.get(stringPattern);

      if (route == null) {
        route = new Route(method, stringPattern);
        routes.put(stringPattern, route);
      }

      route.addHandler(handler);

    } catch (Exception e) {
      VertxLocator.container.getLogger().error("Could not create route", e);
    }
  }

  public RouteMatcher getRouteMatcher(String method, String url) {
    return new RouteMatcher(method, url, this.routes.values());
  }
}
