package org.edgeframework.edge.core.groovy.filters._impl;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.edgeframework.edge.core.groovy.filters.Filter;
import org.edgeframework.edge.core.groovy.filters.FilterContainer;

public class DefaultFilterContainer implements FilterContainer {
  private List<Filter> filters;

  public DefaultFilterContainer() {
    this.filters = new LinkedList<>();
  }

  public void add(Filter filter) {
  }

  @Override
  public Iterator<Filter> iterator() {
    return this.filters.iterator();
  }
}
