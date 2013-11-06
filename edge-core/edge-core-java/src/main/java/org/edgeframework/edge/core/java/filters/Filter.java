package org.edgeframework.edge.core.java.filters;

import org.edgeframework.edge.core.java.http.HttpContext;

public interface Filter {
  public void action(HttpContext context);
}
