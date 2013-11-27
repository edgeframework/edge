package org.edgeframework.edge.core.groovy.filters._impl;

import java.util.Iterator;

import org.edgeframework.edge.core._internal.filters.FilterContainerInternal;
import org.edgeframework.edge.core._internal.filters.FilterInternal;
import org.edgeframework.edge.core._internal.filters._impl.DefaultFilterContainerInternal;
import org.edgeframework.edge.core.groovy.filters.Filter;
import org.edgeframework.edge.core.groovy.filters.FilterContainer;

public class DefaultFilterContainer implements FilterContainer, FilterContainerInternal {
  private DefaultFilterContainerInternal jFilters;

  public DefaultFilterContainer(DefaultFilterContainerInternal jFilters) {
    this.jFilters = jFilters;
  }

  public void add(Filter filter) {
  }

  @Override
  public Iterator<FilterInternal> iterator() {
    return this.jFilters.iterator();
  }
}
