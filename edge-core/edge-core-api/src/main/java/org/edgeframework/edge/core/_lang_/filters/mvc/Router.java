package org.edgeframework.edge.core._lang_.filters.mvc;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.edgeframework.edge.core._lang_.http.Context;
import org.edgeframework.edge.core._lang_.http.Filter;
import org.edgeframework.edge.core._lang_.http.HttpRequest;

public class Router implements Filter {
  private List<RouteMapping> routes = new LinkedList<>();;

  /* Routing Functions */
  public void get(String route, Controller controller, String action) {
    this.add(this.compile(route), "GET", controller, action);
  }

  public void post(String route, Controller controller, String action) {
    this.add(this.compile(route), "POST", controller, action);
  }

  public void put(String route, Controller controller, String action) {
    this.add(this.compile(route), "PUT", controller, action);
  }

  public void delete(String route, Controller controller, String action) {
    this.add(this.compile(route), "DELETE", controller, action);
  }

  private void add(Pattern pattern, String method, Controller controller, String action) {
    RouteMapping mapping = new RouteMapping(pattern, method, controller, action);
    this.routes.add(mapping);
  }

  private Pattern compile(String route) {
    return Pattern.compile(Pattern.quote(route));
  }

  private RouteMapping match(HttpRequest request) {
    for (RouteMapping mapping : routes) {
      if (mapping.handles(request)) {
        return mapping;
      }
    }

    return null;
  }

  /* Filter Override */
  @Override
  public void call(Context context) {
    RouteMapping mapping = this.match(context.getRequest());

    if (mapping != null) {
      ActionResult result = mapping.handle();
      result.action(context);
    } else {
      context.next();
    }
  }
}