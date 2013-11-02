package org.edgeframework.edge.core.api.filters;

import org.edgeframework.edge.core.api.http.Context;

public interface Filter {
  public void action(Context<?> context);
}
