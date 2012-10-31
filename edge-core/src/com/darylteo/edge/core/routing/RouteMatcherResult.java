package com.darylteo.edge.core.routing;

import java.util.HashMap;
import java.util.Map;

public class RouteMatcherResult {
  public final boolean matches;

  public final Route route;
  public final Map<String, String> params;

  public RouteMatcherResult(boolean matches, Route route) {
    this(matches, route, new HashMap<String, String>());
  }

  public RouteMatcherResult(boolean matches, Route route, Map<String, String> params) {
    this.matches = matches;
    this.route = route;

    this.params = params;
  }
}
