package org.edgeframework.edge.core._internal.delegates;

public interface AppDelegateInternal<A> {
  public void afterStart(A app);

  public void beforeStart(A app);

  public void beforeStop(A app);

  public void onError(A app, Throwable e);
}
