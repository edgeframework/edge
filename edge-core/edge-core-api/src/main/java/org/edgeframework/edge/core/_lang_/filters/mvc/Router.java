package org.edgeframework.edge.core._lang_.filters.mvc;

import java.util.LinkedList;
import java.util.List;

import org.edgeframework.edge.core._lang_.http.Context;
import org.edgeframework.edge.core._lang_.http.Filter;
import org.edgeframework.edge.core._lang_.http.HttpRequest;

public class Router implements Filter {
  private List<RouteMapping> routes = new LinkedList<>();;

  /* Routing Functions */
  public void get(String route, Controller controller, String action) {
    this.add(route, "GET", controller, action);
  }

  public void post(String route, Controller controller, String action) {
    this.add(route, "POST", controller, action);
  }

  public void put(String route, Controller controller, String action) {
    this.add(route, "PUT", controller, action);
  }

  public void delete(String route, Controller controller, String action) {
    this.add(route, "DELETE", controller, action);
  }

  private void add(String pattern, String method, Controller controller, String action) {
    RouteMapping mapping = new RouteMapping(pattern, method, controller, action);
    this.routes.add(mapping);
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