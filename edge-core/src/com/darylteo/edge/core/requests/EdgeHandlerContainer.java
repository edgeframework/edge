package com.darylteo.edge.core.requests;

import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.deploy.impl.VertxLocator;

import com.darylteo.edge.core.routing.RouteMatcher;
import com.darylteo.edge.core.routing.RouteMatcherResult;

public class EdgeHandlerContainer {
  private final EdgeRequest request;
  private final EdgeResponse response;

  private final RouteMatcher routeMatcher;

  public EdgeHandlerContainer(HttpServerRequest request, RouteMatcher routeMatcher) {
    this.routeMatcher = routeMatcher;
    this.request = new EdgeRequest(request);
    this.response = new EdgeResponse(request.response);
  }

  public boolean handle() {
    boolean handled = false;

    RouteMatcherResult result = routeMatcher.getNextMatch();

    if (result == null) {
      return handled;
    }

    VertxLocator.container.getLogger().info("Route Match for " + this.request.path);

    this.request.setParams(result.params);

    result.route.getHandler().handleRequest(this.request, this.response);
    handled = true;

    return handled;
  }

}
