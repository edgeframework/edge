package org.edgeframework.edge.core._internal.filters._impl;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.edgeframework.edge.core._internal.filters.FilterContainerInternal;
import org.edgeframework.edge.core._internal.filters.FilterInternal;

public class DefaultFilterContainerInternal implements FilterContainerInternal {
  private List<FilterInternal> filters;

  public DefaultFilterContainerInternal() {
    this.filters = new LinkedList<>();
  }

  public void add(FilterInternal filter) {
    this.filters.add(filter);
  }

  @Override
  public Iterator<FilterInternal> iterator() {
    return this.filters.iterator();
  }
}
