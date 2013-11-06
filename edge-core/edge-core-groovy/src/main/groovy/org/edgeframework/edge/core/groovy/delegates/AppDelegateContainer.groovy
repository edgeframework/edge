package org.edgeframework.edge.core.groovy.delegates

public class AppDelegateContainer implements Iterable<AppDelegate> {
  private org.edgeframework.edge.core.java.delegates.AppDelegateContainer jDelegates

  public void add(AppDelegate delegate) {
    jDelegates.add(delegate)
  }

  public AppDelegateContainer plus(AppDelegate delegate) {
    jDelegates.add(delegate)
  }

  @Override
  public Iterator<AppDelegate> iterator() {
  }

  public AppDelegateContainer(org.edgeframework.edge.core.java.delegates.AppDelegateContainer jDelegates){
    this.jDelegates = jDelegates
  }
}