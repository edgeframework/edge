package org.edgeframework.edge.core._lang_.filters.mvc;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.regex.Pattern;

import org.edgeframework.edge.core._lang_.http.HttpRequest;

public class RouteMapping {
  private Pattern pattern;
  private String method;

  private Controller controller;
  private MethodHandle handle;

  public Controller getController() {
    return this.controller;
  }

  public RouteMapping(Pattern pattern, String method, Controller controller, String action) {
    this.pattern = pattern;
    this.method = method.toUpperCase();
    this.controller = controller;

    try {
      this.handle = MethodHandles.lookup().findVirtual(controller.getClass(), action, MethodType.methodType(ActionResult.class));
    } catch (Exception e) {
      e.printStackTrace();
    }
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

    return this.pattern.matcher(path).matches();
  }

  public ActionResult handle() {
    try {
      return (ActionResult) this.handle.invoke(this.controller);
    } catch (Throwable e) {
      e.printStackTrace();
      return null;
    }
  }
}