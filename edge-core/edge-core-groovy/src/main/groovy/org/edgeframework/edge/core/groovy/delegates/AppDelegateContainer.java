package org.edgeframework.edge.core.groovy.delegates;

import java.util.Map;

public interface AppDelegateContainer {
  public AppDelegateContainer add(AppDelegate delegate);

  public AppDelegateContainer add(Map<String, Object> delegate);
}