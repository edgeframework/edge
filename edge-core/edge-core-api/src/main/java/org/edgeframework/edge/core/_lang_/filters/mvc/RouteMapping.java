package org.edgeframework.edge.core._lang_.filters.mvc;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RouteMapping {
  private CompilationInfo info;

  public Pattern getPattern() {
    return this.info.pattern;
  }

  public List<String> getParams() {
    return this.info.params;
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
    this.method = method.toUpperCase();
    this.controller = controller;

    // this initialises the Regex and Param keys
    this.info = this.compile(pattern);

    try {
      this.handle = retrieveHandle(controller.getClass(), action, this.getParams());
      System.out.printf("%s (%s.%s()) mapped to %s\n", pattern, controller.getClass().getName(), action, this.handle.type().toMethodDescriptorString());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private CompilationInfo compile(String pattern) {
    CompilationInfo result = new CompilationInfo();

    // find all :<id> parameters in route
    Matcher paramMatcher = Pattern.compile(":([^/]+)(/?)").matcher(pattern);
    List<String> params = new LinkedList<>();

    while (paramMatcher.find()) {
      params.add(paramMatcher.group(1));
    }

    // replace all :<id> with regex .* capture group
    String regex = paramMatcher.replaceAll("(?<$1>.*?)$2");
    // String regex = "^" + pattern.replaceAll(":([^/]+)(/?)", "(?<$1>.*?)$2") +
    // "$";

    result.pattern = Pattern.compile(regex);
    result.params = params;

    return result;
  }

  private MethodHandle retrieveHandle(Class<?> clazz, String action, List<String> params) throws NoSuchMethodException, IllegalAccessException {
    List<Class<?>> paramTypes = new LinkedList<>();
    for (String key : params) {
      paramTypes.add(String.class);
    }

    MethodType type = MethodType.methodType(ActionResult.class, paramTypes);
    return MethodHandles.lookup().findVirtual(clazz, action, type);
  }

  private class CompilationInfo {
    Pattern pattern;
    List<String> params;
  }
}