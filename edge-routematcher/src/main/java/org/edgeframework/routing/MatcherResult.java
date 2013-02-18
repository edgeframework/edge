package org.edgeframework.routing;

import java.util.HashMap;
import java.util.Map;

public class MatcherResult {
  public final RouteDefinition route;
  public final Map<String, Object> params;

  public MatcherResult(RouteDefinition route) {
    this(route, new HashMap<String, Object>());
  }

  public MatcherResult(RouteDefinition route, Map<String, Object> params) {
    this.route = route;

    this.params = params;
  }
}
