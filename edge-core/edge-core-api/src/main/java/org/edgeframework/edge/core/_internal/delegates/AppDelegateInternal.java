package org.edgeframework.edge.core._internal.delegates;

import org.edgeframework.edge.core._internal.ApplicationInternal;

public interface AppDelegateInternal {
  public void afterStart(ApplicationInternal app);

  public void beforeStart(ApplicationInternal app);

  public void beforeStop(ApplicationInternal app);

  public void onError(ApplicationInternal app, Throwable e);
}
