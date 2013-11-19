package org.edgeframework.edge.core.groovy.delegates

public class AppDelegateContainer implements Iterable<AppDelegate> {
  private org.edgeframework.edge.core.java.delegates.AppDelegateContainer jDelegates
  private def emptyClosure = {
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


  @Override
  public Iterator<AppDelegate> iterator() {
  }

  public AppDelegateContainer(org.edgeframework.edge.core.java.delegates.AppDelegateContainer jDelegates){
    this.jDelegates = jDelegates
  }
}