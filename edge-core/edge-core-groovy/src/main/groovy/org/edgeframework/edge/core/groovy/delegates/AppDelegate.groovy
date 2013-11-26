package org.edgeframework.edge.core.groovy.delegates

import org.edgeframework.edge.core._internal.ApplicationInternal
import org.edgeframework.edge.core._internal.delegates.AppDelegateInternal
import org.edgeframework.edge.core.groovy.Application

public interface AppDelegate extends AppDelegateInternal {
  public void afterStart(Application app)

  public void beforeStart(Application app)

  public void beforeStop(Application app)

  public void onError(ApplicationInternal app, Throwable e)
}
