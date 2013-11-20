package org.edgeframework.edge.core._internal.filters;

public interface FilterContainer extends Iterable<FilterInternal> {
  public void add(FilterInternal filter);
}
