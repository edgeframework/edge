package org.edgeframework.routing;

import java.util.HashMap;
import java.util.Map;

public class MatcherResult {
  public final Route route;
  public final Map<String, Object> params;

  public MatcherResult(Route route) {
    this(route, new HashMap<String, Object>());
  }

  public MatcherResult(Route route, Map<String, Object> params) {
    this.route = route;

    this.params = params;
  }
}
