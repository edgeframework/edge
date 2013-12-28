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
    this.add(this.compile(route), "GET", controller);
  }

  public void post(String route, Controller controller, String action) {
    this.add(this.compile(route), "POST", controller);
  }

  public void put(String route, Controller controller, String action) {
    this.add(this.compile(route), "PUT", controller);
  }

  public void delete(String route, Controller controller, String action) {
    this.add(this.compile(route), "DELETE", controller);
  }

  private void add(Pattern pattern, String method, Controller controller) {
    RouteMapping mapping = new RouteMapping(pattern, method, controller);
    this.routes.add(mapping);
  }

  private Pattern compile(String route) {
    return Pattern.compile(Pattern.quote(route));
  }

  private Controller match(HttpRequest request) {
    for (RouteMapping mapping : routes) {
      if (mapping.handles(request)) {
        return mapping.getController();
      }
    }

    return null;
  }

  /* Filter Override */
  @Override
  public void call(Context context) {
    Controller controller = this.match(context.getRequest());

    if (controller != null) {
      ActionResult result = controller.ok("Hello World");
      result.action(context);
    } else {
      context.next();
    }
  }
}