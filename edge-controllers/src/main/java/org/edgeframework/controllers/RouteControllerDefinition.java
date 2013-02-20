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
  private MethodHandle handle;

  // Holds a list of parameters by name, and in the order in which the
  // controller is expecting them
  private String[] namedParameters;

  public RouteControllerDefinition(String method, String route, String controller) throws Exception {
    super();
    this.method = method.toUpperCase();
    this.route = route;

    this.controller = controller;
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

  public Result invoke(Map<String, Object> arguments) throws Exception {
    try {
      if (arguments == null) {
        // TODO: Why can't I use invokeExact here? The return type?
        return (Result) this.handle.invokeWithArguments();
      }

      Object[] args = new Object[namedParameters.length];
      for (int i = 0; i < namedParameters.length; i++) {
        args[i] = arguments.get(this.namedParameters[i]);
      }

      return (Result) this.handle.invokeWithArguments(args);
    } catch (Throwable e) {
      throw new Exception("Could not invoke controller action", e);
    }
  }

  public Result invoke() throws Exception {
    return this.invoke(null);
  }

  private void parseController(String definition) throws Exception {
    int delimiterIndex = definition.lastIndexOf('.');

    String controllerName = definition.substring(0, delimiterIndex);
    String actionMethod = definition.substring(delimiterIndex + 1);

    Matcher matcher = SIGNATURE_REGEX.matcher(actionMethod);
    if (!matcher.matches()) {
      throw new Exception("Could not parse controller action: " + definition);
    }

    String actionName = matcher.group("name");
    String actionParams[] = matcher.group("params").split("\\s*,\\s*");

    MethodHandles.Lookup lookup = MethodHandles.lookup();
    Class<?> clazz = Class.forName(controllerName);

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
    this.handle = lookup.findStatic(clazz, actionName, mt);
  }

}
