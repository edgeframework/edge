package org.edgeframework.edge.core._lang_.filters.mvc;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.regex.Pattern;

public class RouteMapping {
  private Pattern pattern;

  public Pattern getPattern() {
    return this.pattern;
  }

  private String method;

  public String getMethod() {
    return this.method;
  }

  private Controller controller;

  public Controller getController() {
    return this.controller;
  }

  private MethodHandle handle;

  public MethodHandle getHandle() {
    return this.handle;
  }

  public RouteMapping(String pattern, String method, Controller controller, String action) {
    this.pattern = this.compile(pattern);
    this.method = method.toUpperCase();
    this.controller = controller;

    System.out.printf("Route Compiled %s to %s\n", pattern, this.pattern.toString());

    try {
      this.handle = MethodHandles.lookup().findVirtual(controller.getClass(), action, MethodType.methodType(ActionResult.class));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private Pattern compile(String pattern) {
    // replace all :<id> with regex .* capture group
    String regex = "^" + pattern.replaceAll(":([^/]+)(/?)", "(?<$1>.*?)$2") + "$";
    return Pattern.compile(regex);
  }
}