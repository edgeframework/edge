package org.edgeframework.edge.core._lang_.app;

public interface ApplicationErrorEventHandler {
  public void call(Application app, Throwable e);
}
