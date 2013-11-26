package org.edgeframework.edge.core.groovy.filters._impl

import groovy.transform.CompileStatic

import org.edgeframework.edge.core._internal.filters.FilterContainerInternal
import org.edgeframework.edge.core._internal.filters.FilterInternal
import org.edgeframework.edge.core._internal.filters._impl.DefaultFilterContainerInternal
import org.edgeframework.edge.core._internal.http.HttpContextInternal
import org.edgeframework.edge.core.groovy.filters.Filter
import org.edgeframework.edge.core.groovy.filters.FilterContainer
import org.edgeframework.edge.core.groovy.http.HttpContext

@CompileStatic
public class DefaultFilterContainer implements FilterContainer, FilterContainerInternal {
  private DefaultFilterContainerInternal jFilters

  public DefaultFilterContainer() {
    jFilters = new DefaultFilterContainerInternal()
  }

  @Override
  public void add(Filter filter) {
    jFilters.add({HttpContextInternal context ->
      filter.action(new HttpContext(context))
    } as FilterInternal)
  }

  @Override
  public Iterator<FilterInternal> iterator() {
    return jFilters.filters.iterator()
  }
}
