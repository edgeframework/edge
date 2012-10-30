package com.darylteo.edge.core.routing;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.vertx.java.core.Handler;

import com.darylteo.edge.core.EdgeRequest;

public class Routing {
  private final List<Route> routes;

  public Routing() {
    this.routes = new LinkedList<>();
  }

  public void addRoute(String method, Pattern pattern, Handler<EdgeRequest> handler) {
    routes.add(new Route(method, pattern, handler));
  }

  public RouteMatcher getRouteMatcher(String method, String url) {
    return new RouteMatcher(method, url, new LinkedList<>(this.routes));
  }
}
