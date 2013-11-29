package org.edgeframework.edge.core.groovy;

import org.edgeframework.edge.core.api._Application;
import org.edgeframework.edge.core.groovy.delegates.AppDelegate;
import org.edgeframework.edge.core.groovy.delegates.AppDelegateContainer;

public interface Application extends _Application<Application, AppDelegateContainer, AppDelegate> {
  @Override
  public AppDelegateContainer getDelegates();
}
