package org.edgeframework.edge.core._internal.filters;

import org.edgeframework.edge.core._internal.http.HttpContextInternal;

public interface FilterInternal {
  public void action(HttpContextInternal context);
}
