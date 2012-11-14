package com.darylteo.edge.core.routing;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

public class RouteMatcher {

  private final String method;
  private final String url;

  private final Iterator<Route> iterator;

  public RouteMatcher(String method, String url, Collection<Route> routes) {
    this.method = method;
    this.url = url;

    this.iterator = new LinkedList<Route>(routes).iterator();
  }

  /**
   * Returns the next matched handler. Returns null if none left.
   * 
   * @return the route handler
   */
  public RouteMatcherResult getNextMatch() {
    while (iterator.hasNext()) {
      Route route = iterator.next();

      RouteMatcherResult result = route.matches(this.method, this.url);
      if (result.matches) {
        return result;
      }
    }

    return null;
  }
}
