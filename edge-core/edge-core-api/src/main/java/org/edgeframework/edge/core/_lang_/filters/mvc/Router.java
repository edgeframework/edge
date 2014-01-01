package org.edgeframework.edge.core._lang_.filters.mvc;

import java.lang.invoke.MethodHandle;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;

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

  /* Filter Override */
  @Override
  public void call(Context context) {
    // vertx returns empty if the root path is requested so lets turn
    // it into / for consistency
    HttpRequest request = context.getRequest();
    String method = request.getMethod();
    String path = request.getPath();
    if (path.isEmpty()) {
      path = "/";
    }

    for (RouteMapping mapping : this.routes) {
      if (mapping.getMethod().equals(method)) {
        continue;
      }

      Matcher matcher = mapping.getPattern().matcher(path);
      if (!matcher.matches()) {
        continue;
      }

      try {
        Controller controller = mapping.getController();
        MethodHandle handle = mapping.getHandle();

        ActionResult result = (ActionResult) handle.invoke(controller);
        result.action(context);
      } catch (Throwable e) {
        // TODO: Handle error
        e.printStackTrace();
        context.next();
      }
    }

    context.next();
  }
}