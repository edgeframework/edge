package com.darylteo.edge.core.routing;

import java.util.LinkedList;
import java.util.List;

import org.vertx.java.deploy.impl.VertxLocator;

import com.darylteo.edge.core.requests.EdgeHandler;

public class Routing {
  private final List<Route> routes;

  public Routing() {
    this.routes = new LinkedList<>();
  }

  public void addRoute(String method, String stringPattern, EdgeHandler... handlers) {
    try {
      Route route = new Route(method, stringPattern, handlers);
      this.routes.add(route);
    } catch (Exception e) {
      VertxLocator.container.getLogger().error("Could not create route", e);
    }
  }

  public RouteMatcher getRouteMatcher(String method, String url) {
    return new RouteMatcher(method, url, this.routes);
  }
}
