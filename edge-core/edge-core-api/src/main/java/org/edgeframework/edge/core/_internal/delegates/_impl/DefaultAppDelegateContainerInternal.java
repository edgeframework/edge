package org.edgeframework.edge.core._internal.delegates._impl;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.edgeframework.edge.core._internal.delegates.AppDelegateContainerInternal;
import org.edgeframework.edge.core._internal.delegates.AppDelegateInternal;

public class DefaultAppDelegateContainerInternal implements AppDelegateContainerInternal {
  private List<AppDelegateInternal> delegates;

  public DefaultAppDelegateContainerInternal() {
    delegates = new LinkedList<>();
  }

  public void add(AppDelegateInternal delegate) {
    this.delegates.add(delegate);
  }

  public void remove(AppDelegateInternal delegate) {
    this.delegates.remove(delegate);
  }

  @Override
  public Iterator<AppDelegateInternal> iterator() {
    return delegates.iterator();
  }
}
