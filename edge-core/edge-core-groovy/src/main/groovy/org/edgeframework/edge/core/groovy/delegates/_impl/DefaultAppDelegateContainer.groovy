package org.edgeframework.edge.core.groovy.delegates._impl

import groovy.transform.CompileStatic

import org.edgeframework.edge.core._internal.delegates.AppDelegateContainerInternal
import org.edgeframework.edge.core._internal.delegates._impl.DefaultAppDelegateContainerInternal
import org.edgeframework.edge.core.groovy.delegates.AppDelegate
import org.edgeframework.edge.core.groovy.delegates.AppDelegateContainer

@CompileStatic
public class DefaultAppDelegateContainer implements AppDelegateContainer, AppDelegateContainerInternal {
  private DefaultAppDelegateContainerInternal jDelegates

  private final def emptyClosure = {
  }

  public void add(AppDelegate delegate) {
    jDelegates.add(delegate)
  }

  public AppDelegateContainer plus(AppDelegate delegate) {
    this.add(delegate)
  }

  public AppDelegateContainer add(Map delegate) {
    delegate.beforeStart = delegate.beforeStart ?: emptyClosure
    delegate.afterStart = delegate.afterStart ?: emptyClosure
    delegate.beforeStop = delegate.beforeStop ?: emptyClosure
    delegate.onError = delegate.onError ?: emptyClosure

    this.add(delegate as AppDelegate)
  }

  public AppDelegateContainer plus(Map delegate) {
    this.add(delegate)
  }

  public AppDelegateContainer(DefaultAppDelegateContainerInternal jDelegates){
    this.jDelegates = jDelegates
  }

  @Override
  public Iterator<AppDelegate> iterator() {
    return jDelegates.iterator()
  }
}
