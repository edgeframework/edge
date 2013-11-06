package org.edgeframework.edge.core.java.filters._internal;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.edgeframework.edge.core.java.filters.Filter;
import org.edgeframework.edge.core.java.filters.FilterContainer;

public class DefaultFilterContainer implements FilterContainer {
  private List<Filter> filters;

  @Override
  public void add(Filter filter) {
    this.filters.add(filter);
  }

  public DefaultFilterContainer() {
    this.filters = new LinkedList<>();
  }

  @Override
  public Iterator<Filter> iterator() {
    return this.filters.iterator();
  }
}
