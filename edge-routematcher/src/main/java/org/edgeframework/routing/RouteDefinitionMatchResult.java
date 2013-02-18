package org.edgeframework.routing;

import java.util.HashMap;
import java.util.Map;

class RouteDefinitionMatchResult {
  public final RouteDefinition route;
  public final Map<String, Object> params;

  public RouteDefinitionMatchResult(RouteDefinition route) {
    this(route, new HashMap<String, Object>());
  }

  public RouteDefinitionMatchResult(RouteDefinition route, Map<String, Object> params) {
    this.route = route;

    this.params = params;
  }
}
