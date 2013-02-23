package org.edgeframework.controllers;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.edgeframework.routing.HttpServerRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.Vertx;

public class RouteControllerDefinition {

  private static Logger logger = LoggerFactory.getLogger(RouteControllerDefinition.class);

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

  private static final Map<String, Object> emptybody = Collections.emptyMap();

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

  public Result invoke(Vertx vertx, HttpServerRequest request) throws Exception {
    return this.invoke(vertx, (Controller) this.controllerClass.newInstance(), request);
  }

  private Result invoke(Vertx vertx, Controller receiver, HttpServerRequest request) throws Exception {
    try {
      receiver.setVertx(vertx);

      // + 1 to hold receiving target
      Object[] args = new Object[this.namedParameters.length + 1];

      Map<String, Object> params = request.getParams();
      Map<String, Object> query = request.getQuery();
      Map<String, Object> body = request.getBody();
      body = body != null ? body : RouteControllerDefinition.emptybody;

      args[0] = receiver;
      for (int i = 0; i < this.namedParameters.length; i++) {
        String name = this.namedParameters[i];

        // Route Parameters take top priority
        // Followed by Post Data
        // Then Query String data
        // Thus, go through in ascending order of priority
        // And thus overwrite lower priority values

        if (body.containsKey(name)) {
          args[i + 1] = body.get(name);
        }

        if (query.containsKey(name)) {
          args[i + 1] = query.get(name);
        }

        if (params.containsKey(name)) {
          args[i + 1] = params.get(name);
        }

        logger.info("Parameter: " + name + " = " + args[i + 1]);
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
