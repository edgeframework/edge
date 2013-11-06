package org.edgeframework.edge.core.java.filters;

public interface FilterContainer extends Iterable<Filter> {
  public void add(Filter filter);
}
