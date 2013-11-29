package org.edgeframework.edge.core.api;

import org.edgeframework.edge.core.api.delegates._AppDelegate;
import org.edgeframework.edge.core.api.delegates._AppDelegateContainer;

public class _ApplicationEngine<A extends _Application<A, ? extends _AppDelegateContainer<A, ? extends _AppDelegate<A>>, ? extends _AppDelegate<A>>> {
  private A app;

  public _ApplicationEngine(A app) {
    this.app = app;
  }

  public void start() {
    for (_AppDelegate<A> delegate : app.getDelegates()) {
      delegate.onStart(app);
    }
  }
}
