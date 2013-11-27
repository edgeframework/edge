package org.edgeframework.edge.core.groovy.filters;

import org.edgeframework.edge.core.groovy.http.HttpContext;

public interface Filter {
  public void action(HttpContext context);
}
