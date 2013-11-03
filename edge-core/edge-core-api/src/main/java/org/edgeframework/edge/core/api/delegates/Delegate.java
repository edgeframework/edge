package org.edgeframework.edge.core.api.delegates;

import org.edgeframework.edge.core.api.Application;

public interface Delegate<A extends Application> {
  public void afterStart(A app);

  public void beforeStart(A app);

  public void beforeStop(A app);

  public void onError(Throwable e);
}
