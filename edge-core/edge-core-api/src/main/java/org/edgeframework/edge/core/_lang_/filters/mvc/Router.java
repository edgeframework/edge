package org.edgeframework.edge.core._lang_.filters.mvc;

import org.edgeframework.edge.core._lang_.http.Filter;
import org.edgeframework.edge.core._lang_.http.Context;

public class Router implements Filter {
  private RouteContainer routes = new RouteContainer();

  public RouteContainer getRoutes() {
    return this.routes;
  }

  @Override
  public void call(Context context) {
    Controller controller = match(context.getRequest().getPath());
    
    if (controller != null) {
      
    }
  }

  private Controller match(String path) {
    return new Controller();
  }
}