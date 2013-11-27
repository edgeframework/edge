package org.edgeframework.edge.core.groovy.delegates._impl;

import groovy.lang.Closure;
import groovy.transform.CompileStatic;

import java.util.Map;

import org.edgeframework.edge.core._internal.delegates.AppDelegateInternal;
import org.edgeframework.edge.core._internal.delegates._impl.DefaultAppDelegateContainerInternal;
import org.edgeframework.edge.core.groovy.Application;
import org.edgeframework.edge.core.groovy.delegates.AppDelegate;
import org.edgeframework.edge.core.groovy.delegates.AppDelegateContainer;

public class DefaultAppDelegateContainer extends DefaultAppDelegateContainerInternal<AppDelegate> implements AppDelegateContainer {
  @SuppressWarnings("serial")
  private final Closure<Void> emptyClosure = new Closure<Void>(null, null) {
  };

  public DefaultAppDelegateContainer() {
  }

  public DefaultAppDelegateContainer add(AppDelegate delegate) {
    this.jDelegates.add(new AppDelegateWrapper(delegate));
    return this;
  }

  @Override
  public AppDelegateContainer add(Map<String, Object> delegate) {
    delegate.put("beforeStart", delegate.containsKey("beforeStart") ? delegate.get("beforeStart") : emptyClosure);
    delegate.put("afterStart", delegate.containsKey("afterStart") ? delegate.get("afterStart") : emptyClosure);
    delegate.put("beforeStop", delegate.containsKey("beforeStop") ? delegate.get("beforeStop") : emptyClosure);
    delegate.put("onError", delegate.containsKey("onError") ? delegate.get("onError") : emptyClosure);

    this.add(delegate);

    return this;
  }

  @CompileStatic
  private class AppDelegateWrapper implements AppDelegateInternal<Application> {
    private AppDelegate delegate;

    public AppDelegateWrapper(AppDelegate delegate) {
      this.delegate = delegate;
    }

    @Override
    public void beforeStart(Application app) {
      // TODO Auto-generated method stub

    }

    @Override
    public void afterStart(Application app) {
      // TODO Auto-generated method stub

    }

    @Override
    public void beforeStop(Application app) {
      // TODO Auto-generated method stub

    }

    @Override
    public void onError(Application app, Throwable e) {
      // TODO Auto-generated method stub
    }
  }
}
