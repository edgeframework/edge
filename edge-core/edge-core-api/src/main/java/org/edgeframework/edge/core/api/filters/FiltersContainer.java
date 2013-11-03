package org.edgeframework.edge.core.api.filters;

import java.util.List;

/**
 * Indicates a class that contains a list of request Filters
 * 
 * @author Daryl Teo
 * 
 */
public interface FiltersContainer {
  public List<Filter> getFilters();

  public List<Filter> filters();
}
