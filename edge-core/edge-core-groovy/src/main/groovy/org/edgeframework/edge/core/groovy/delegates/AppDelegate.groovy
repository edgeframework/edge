package org.edgeframework.edge.core.groovy.delegates

import org.edgeframework.edge.core.groovy.Application

public interface AppDelegate {
  public void afterStart(Application app)

  public void beforeStart(Application app)

  public void beforeStop(Application app)

  public void onError(Throwable e)
}
