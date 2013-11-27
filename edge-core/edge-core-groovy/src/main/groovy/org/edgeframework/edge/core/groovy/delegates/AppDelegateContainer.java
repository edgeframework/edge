package org.edgeframework.edge.core.groovy.delegates;

import java.util.Map;

public interface AppDelegateContainer {
  public void add(AppDelegate delegate);

  public void add(Map<String, Object> delegate);
}