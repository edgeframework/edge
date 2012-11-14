package com.darylteo.edge.core.requests;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.deploy.impl.VertxLocator;

import com.darylteo.edge.core.routing.RouteMatcher;
import com.darylteo.edge.core.routing.RouteMatcherResult;

public class EdgeHandlerContainer {
  private final HttpServerRequest _request;

  private final List<EdgeHandler> currentHandlers = new LinkedList<>();

  private final RouteMatcher routeMatcher;
  private RouteMatcherResult currentResult;

  public EdgeHandlerContainer(HttpServerRequest request, RouteMatcher routeMatcher) {
    this.routeMatcher = routeMatcher;

    this._request = request;

    this.next();
    this.handle();
  }

  void next() {
    this.currentHandlers.clear();

    this.currentResult = routeMatcher.getNextMatch();

    if (this.currentResult == null) {
      return;
    }

    this.currentHandlers.addAll(Arrays.asList(this.currentResult.route.getHandlers()));
  }

  private void handle() {
    Map<String, Object> params = new HashMap<>();

    EdgeRequest request = new EdgeRequest(_request, params);
    EdgeResponse response = new EdgeResponse(_request.response);

    while (!this.currentHandlers.isEmpty()) {
      VertxLocator.container.getLogger().info("Handling: " + request.getPath());

      EdgeHandler handler = this.currentHandlers.remove(0);
      handler.handle(this, request, response);
    }
  }
}
