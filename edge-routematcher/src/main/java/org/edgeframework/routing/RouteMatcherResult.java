package org.edgeframework.routing;

import java.util.HashMap;
import java.util.Map;

public class RouteMatcherResult {
  public final Route route;
  public final Map<String, Object> params;

  public RouteMatcherResult(Route route) {
    this(route, new HashMap<String, Object>());
  }

  public RouteMatcherResult(Route route, Map<String, Object> params) {
    this.route = route;

    this.params = params;
  }
}
