package org.edgeframework.edge.core._lang_.filters.mvc;

import java.util.regex.Pattern;

import org.edgeframework.edge.core._lang_.http.HttpRequest;

public class RouteMapping {
  private Pattern pattern;
  private String method;
  private Controller controller;

  public Controller getController() {
    return this.controller;
  }

  public RouteMapping(Pattern pattern, String method, Controller controller) {
    this.pattern = pattern;
    this.method = method.toUpperCase();
    this.controller = controller;
  }

  public boolean handles(HttpRequest request) {
    if (!request.getMethod().equals(this.method)) {
      return false;
    }

    // vertx returns empty if the root path is requested so lets turn
    // it into / for consistency
    String path = request.getPath();
    if (path.isEmpty()) {
      path = "/";
    }
    System.out.println("Request check: " + path);

    return this.pattern.matcher(path).matches();
  }
}