package org.edgeframework.core.api.routing;

import org.edgeframework.core.filters.Filter;

public abstract class Router extends Filter {
  public abstract Router register(String name, String pattern, Class<?> controllerClass);
}
