package org.edgeframework.edge.core.java.delegates;

import org.edgeframework.edge.core.java.Application;

public class Delegate implements org.edgeframework.edge.core.api.delegates.Delegate<Application> {

  @Override
  public void afterStart(Application app) {
  }

  @Override
  public void beforeStart(Application app) {
  }

  @Override
  public void beforeStop(Application app) {
  }

  @Override
  public void onError(Throwable e) {
  }
}
