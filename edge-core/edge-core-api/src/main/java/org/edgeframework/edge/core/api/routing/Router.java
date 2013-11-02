package org.edgeframework.edge.core.api.routing;

import org.edgeframework.edge.core.api.filters.Filter;

public abstract class Router implements Filter {
  public abstract Router register(String name, String pattern, Class<?> controllerClass);
}
