package org.edgeframework.routing;

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
    RouteMatcherResult result = null;
    while (iterator.hasNext() && result == null) {
      result = iterator.next().matches(this.method, this.url);
    }

    return result;
  }
}
