package com.darylteo.edge.core.routing;

import java.util.List;

public class RouteMatcher {

  private final String method;
  private final String url;

  private final List<Route> routes;
  private int position = 0;

  public RouteMatcher(String method, String url, List<Route> routes) {
    this.method = method;
    this.url = url;

    this.routes = routes;
    this.position = 0;
  }

  public RouteMatcherResult getNextMatch() {
    while (this.position < this.routes.size()) {
      Route route = this.routes.get(this.position);

      this.position++;

      RouteMatcherResult result = route.matches(this.method, this.url);
      if (result.matches) {
        return result;
      }
    }

    return null;
  }
}
