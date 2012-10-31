package com.darylteo.edge.core.requests;

import java.util.HashMap;
import java.util.Map;

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

    this.request.setQuery(this.queryToMap(request.query));
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

  private Map<String, String> queryToMap(String queryString) {
    Map<String, String> query = new HashMap<>();

    if (queryString == null) {
      return query;
    }

    for (String pair : queryString.split("&")) {
      String[] keyvaluepair = pair.split("=");
      if (keyvaluepair.length == 2) {
        query.put(keyvaluepair[0], keyvaluepair[1]);
      } else {
        query.put(keyvaluepair[0], null);
      }

    }

    return query;
  }
}
