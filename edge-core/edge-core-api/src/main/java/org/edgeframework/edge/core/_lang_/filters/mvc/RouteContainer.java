package org.edgeframework.edge.core._lang_.filters.mvc;

import java.util.LinkedList;
import java.util.List;

public class RouteContainer {
  private List<String> routes = new LinkedList<>();;

  public RouteContainer add(String route) {
    this.routes.add(route);
    return this;
  }
}