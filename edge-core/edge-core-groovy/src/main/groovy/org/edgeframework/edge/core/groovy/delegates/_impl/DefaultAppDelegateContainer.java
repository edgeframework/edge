package org.edgeframework.edge.core.groovy.delegates._impl;

import groovy.lang.Closure;

import java.util.Map;

import org.edgeframework.edge.core._internal.delegates.AppDelegateContainerInternal;
import org.edgeframework.edge.core.groovy.delegates.AppDelegate;
import org.edgeframework.edge.core.groovy.delegates.AppDelegateContainer;

public class DefaultAppDelegateContainer extends AppDelegateContainerInternal<AppDelegate> implements AppDelegateContainer {
  @SuppressWarnings("serial")
  private final Closure<Void> emptyClosure = new Closure<Void>(null, null) {
  };

  public DefaultAppDelegateContainer() {
  }

  public void add(Map<String, Object> delegate) {
    delegate.put("beforeStart", delegate.containsKey("beforeStart") ? delegate.get("beforeStart") : emptyClosure);
    delegate.put("afterStart", delegate.containsKey("afterStart") ? delegate.get("afterStart") : emptyClosure);
    delegate.put("beforeStop", delegate.containsKey("beforeStop") ? delegate.get("beforeStop") : emptyClosure);
    delegate.put("onError", delegate.containsKey("onError") ? delegate.get("onError") : emptyClosure);

    this.add(delegate);
  }
}
