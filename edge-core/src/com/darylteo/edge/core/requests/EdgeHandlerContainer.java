package com.darylteo.edge.core.requests;

import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.deploy.impl.VertxLocator;

import com.darylteo.edge.core.routing.RouteMatcher;
import com.darylteo.edge.core.routing.RouteMatcherResult;

public class EdgeHandlerContainer {
  private final HttpServerRequest _request;

  private final RouteMatcher routeMatcher;

  public EdgeHandlerContainer(HttpServerRequest request, RouteMatcher routeMatcher) {
    this.routeMatcher = routeMatcher;

    this._request = request;

    this.handle();
  }

  private boolean handle() {
    boolean handled = false;

    RouteMatcherResult result = routeMatcher.getNextMatch();

    if (result == null) {
      return handled;
    }

    EdgeRequest request = new EdgeRequest(_request, result);
    EdgeResponse response = new EdgeResponse(_request.response);

    VertxLocator.container.getLogger().info("Route Match for " + request.getPath());

    EdgeHandler[] handlers = result.route.getHandlers();

    for (EdgeHandler handler : handlers) {
      handler.handleRequest(request, response);
    }

    handled = true;

    return handled;
  }

}
