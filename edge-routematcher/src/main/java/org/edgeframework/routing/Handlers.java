package org.edgeframework.routing;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Handlers {

  private final String method;
  private final String url;

  private final Iterator<RouteDefinitionMatchResult> iterator;

  public Handlers(String method, String url, List<RouteDefinition> routes) {
    this.method = method;
    this.url = url;

    this.iterator = getMatches(method, url, routes).iterator();
  }

  public boolean hasNextMatch() {
    return this.iterator.hasNext();
  }

  /**
   * Returns the next matched handler. Returns null if none left.
   * 
   * @return the route handler
   */
  public RouteDefinitionMatchResult getNextMatch() {
    return this.hasNextMatch() ?
        this.iterator.next() :
        null;
  }

  private List<RouteDefinitionMatchResult> getMatches(String method, String url, List<RouteDefinition> routes) {
    List<RouteDefinitionMatchResult> matches = new LinkedList<>();

    for (RouteDefinition route : routes) {
      RouteDefinitionMatchResult match = route.matches(method, url);
      if (match != null) {
        matches.add(match);
      }
    }

    return matches;
  }
}
