package org.edgeframework.edge.core._lang_.filters.mvc;

import org.edgeframework.edge.core._lang_.http.Context;
import org.edgeframework.edge.core._lang_.http.Filter;

public class Router implements Filter {
  private RouteContainer routes = new RouteContainer();

  public RouteContainer getRoutes() {
    return this.routes;
  }

  @Override
  public void call(Context context) {
    Controller controller = match(context.getRequest().getPath());

    if (controller != null) {
      ActionResult result = controller.ok();
      result.action(context);
    }
  }

  private Controller match(String path) {
    return new Controller();
  }
}