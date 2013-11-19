package org.edgeframework.edge.core.java.delegates;

import org.edgeframework.edge.core.java.Application;

public interface AppDelegate {
  public void afterStart(Application app);

  public void beforeStart(Application app);

  public void beforeStop(Application app);

  public void onError(Application app, Throwable e);
}
