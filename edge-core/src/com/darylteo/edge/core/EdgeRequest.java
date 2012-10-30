package com.darylteo.edge.core;

import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.HttpServerResponse;
import org.vertx.java.deploy.impl.VertxLocator;

import com.darylteo.edge.core.routing.Route;
import com.darylteo.edge.core.routing.RouteMatcher;

public class EdgeRequest {
  public final HttpServerRequest request;
  public final HttpServerResponse response;

  private final RouteMatcher routeMatcher;

  private boolean shouldStop = false;

  public EdgeRequest(HttpServerRequest request, RouteMatcher routeMatcher) {
    this.request = request;
    this.response = request.response;

    this.routeMatcher = routeMatcher;

    this.handle();
  }

  /* Signals that the request should pass through */
  public void lastHandler(boolean value) {
    this.shouldStop = value;
  }

  private boolean handle() {
    boolean handled = false;

    while (true) {
      Route route = routeMatcher.getNextMatch();

      VertxLocator.container.getLogger().info("Route Match:" + route);

      if (route == null) {
        return handled;
      }

      /* ShouldStop is used to determine if this will be the last handler */
      this.shouldStop = false;
      route.getHandler().handle(this);
      handled = true;

      if (this.shouldStop) {
        return handled;
      }
    }
  }

}
