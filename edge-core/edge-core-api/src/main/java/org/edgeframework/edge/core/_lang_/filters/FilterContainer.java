package org.edgeframework.edge.core._lang_.filters;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class FilterContainer implements Iterable<Filter> {
  private List<Filter> filters;

  public FilterContainer() {
    this.filters = new LinkedList<>();
  }

  public void add(Filter filter) {
    this.filters.add(filter);
  }

  @Override
  public Iterator<Filter> iterator() {
    return this.filters.iterator();
  }
}
