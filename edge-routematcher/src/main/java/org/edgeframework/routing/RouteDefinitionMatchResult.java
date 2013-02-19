package org.edgeframework.routing;

import java.util.HashMap;
import java.util.Map;

public class RouteDefinitionMatchResult {
  private final RouteDefinition route;
  private final Map<String, Object> params;

  public RouteDefinitionMatchResult(RouteDefinition route) {
    this(route, new HashMap<String, Object>());
  }

  public RouteDefinitionMatchResult(RouteDefinition route, Map<String, Object> params) {
    this.route = route;

    this.params = params;
  }

  public Map<String, Object> getParams() {
    return params;
  }

  public RouteDefinition getRoute() {
    return route;
  }
}
