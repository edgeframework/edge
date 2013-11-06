package org.edgeframework.edge.core.java.delegates;

public interface AppDelegateContainer extends Iterable<AppDelegate> {
  public void add(AppDelegate delegate);
}