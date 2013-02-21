package org.edgeframework.controllers;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.edgeframework.controllers.test.TestController;
import org.vertx.java.core.Vertx;

public class RouteControllerDefinition {

  private static final Map<String, Class<?>> CLASS_MAPPINGS = new HashMap<>();
  {
    CLASS_MAPPINGS.put("Byte", Byte.class);
    CLASS_MAPPINGS.put("Short", Short.class);
    CLASS_MAPPINGS.put("Integer", Integer.class);
    CLASS_MAPPINGS.put("Long", Long.class);
    CLASS_MAPPINGS.put("Double", Integer.class);
    CLASS_MAPPINGS.put("Float", Integer.class);
  }

  private static final Pattern SIGNATURE_REGEX = Pattern.compile("^(?<name>\\S+)\\((?<params>.*)\\)$");

  private String method;
  private String route;
  private String controller;

  private Class<?> controllerClass;

  private MethodHandle handle;

  // Holds a list of parameters by name, and in the order in which the
  // controller is expecting them
  private String[] namedParameters;

  public RouteControllerDefinition(String method, String route, String controller) throws Exception {
    super();
    this.method = method.toUpperCase();
    this.route = route;

    parseController(controller);
  }

  public String getMethod() {
    return this.method;
  }

  public String getRoute() {
    return this.route;
  }

  public String getController() {
    return this.controller;
  }

  public Result invoke(Vertx vertx, Map<String, Object> arguments) throws Exception {
    return this.invoke(vertx, (Controller) this.controllerClass.newInstance(), arguments);
  }

  public Result invoke(Vertx vertx) throws Exception {
    return this.invoke(vertx, null);
  }

  private Result invoke(Vertx vertx, Controller receiver, Map<String, Object> arguments) throws Exception {
    try {
      receiver.setVertx(vertx);

      // + 1 to hold receiving target
      Object[] args = new Object[this.namedParameters.length + 1];

      args[0] = receiver;
      for (int i = 0; i < this.namedParameters.length; i++) {
        args[i + 1] = arguments.get(this.namedParameters[i]);
      }

      return (Result) this.handle.invokeWithArguments(args);
    } catch (Throwable e) {
      e.printStackTrace();
      throw new Exception("Could not invoke controller action", e);
    }
  }

  private void parseController(String definition) throws Exception {
    int delimiterIndex = definition.lastIndexOf('.');

    // Get both segments of the definition
    String controllerName = definition.substring(0, delimiterIndex);
    String actionMethod = definition.substring(delimiterIndex + 1);

    Matcher matcher = SIGNATURE_REGEX.matcher(actionMethod);
    if (!matcher.matches()) {
      throw new Exception("Could not parse controller action: " + definition);
    }

    String actionName = matcher.group("name");
    String actionParams[] = matcher.group("params").split("\\s*,\\s*");

    this.controllerClass = Class.forName(controllerName);

    // TODO: check hierarchy of Controller

    MethodType mt = MethodType.methodType(Result.class);
    List<String> namedParams = new ArrayList<>(actionParams.length);
    for (String param : actionParams) {
      if (param.isEmpty()) {
        continue;
      }

      String[] parts = param.split(":");

      Class<?> type = String.class;

      if (parts.length > 1 && CLASS_MAPPINGS.containsKey(parts[1])) {
        type = CLASS_MAPPINGS.get(parts[1]);
      }

      mt = mt.appendParameterTypes(type);
      namedParams.add(parts[0]);
    }

    this.namedParameters = namedParams.toArray(new String[namedParams.size()]);
    this.handle = MethodHandles.lookup().findVirtual(this.controllerClass, actionName, mt);
  }

}
