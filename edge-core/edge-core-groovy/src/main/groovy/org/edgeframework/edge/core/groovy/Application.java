package org.edgeframework.edge.core.groovy;

import org.edgeframework.edge.core.groovy.delegates.AppDelegateContainer;
import org.edgeframework.edge.core.groovy.filters.FilterContainer;

public interface Application {
  public AppDelegateContainer getDelegates();

  public FilterContainer getFilters();
}