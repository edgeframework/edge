package org.edgeframework.java.core.routing;

import java.util.regex.Pattern;

import org.edgeframework.java.core.controller.TypeConverter;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class Router {
  private Pattern ROUTE_PATTERN = Pattern.compile("^(?<controller>.*)\\.(?<action>\\w*\\(.*\\))$");

  private TypeConverter converter = new TypeConverter();

  public Router register(String name, String pattern) {
    throw new NotImplementedException();
    // return this;
  }
}
