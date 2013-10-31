package org.edgeframework.core.filters;

import org.edgeframework.core.api.http.Context;

public abstract class Filter {
  public abstract void action(Context context);
}
