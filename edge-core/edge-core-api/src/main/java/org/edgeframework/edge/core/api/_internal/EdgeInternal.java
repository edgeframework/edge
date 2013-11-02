package org.edgeframework.edge.core.api._internal;

import java.util.List;

import org.edgeframework.edge.core.api.filters.Filter;

public interface EdgeInternal {
  /* Lifecycle */
  public void __configure();

  public void __begin();

  /* Properties */
  public List<Filter> __filters();
}
