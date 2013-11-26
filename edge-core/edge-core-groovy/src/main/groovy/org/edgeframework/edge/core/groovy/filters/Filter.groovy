package org.edgeframework.edge.core.groovy.filters

import groovy.transform.CompileStatic

import org.edgeframework.edge.core.groovy.http.HttpContext

@CompileStatic
public interface Filter {
  public void action(HttpContext context)
}
