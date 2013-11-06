package org.edgeframework.edge.core.java.delegates._internal;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.edgeframework.edge.core.java.delegates.AppDelegate;
import org.edgeframework.edge.core.java.delegates.AppDelegateContainer;

public class DefaultAppDelegateContainer implements AppDelegateContainer {
  private List<AppDelegate> delegates;

  @Override
  public void add(AppDelegate delegate) {
    this.delegates.add(delegate);
  }

  public DefaultAppDelegateContainer() {
    this.delegates = new LinkedList<>();
  }

  @Override
  public Iterator<AppDelegate> iterator() {
    return this.delegates.iterator();
  }

}
