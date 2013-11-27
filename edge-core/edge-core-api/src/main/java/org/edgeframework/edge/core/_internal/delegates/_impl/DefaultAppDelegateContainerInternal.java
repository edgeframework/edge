package org.edgeframework.edge.core._internal.delegates._impl;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class DefaultAppDelegateContainerInternal<A> implements Iterable<A> {
  private List<A> delegates;

  public DefaultAppDelegateContainerInternal() {
    delegates = new LinkedList<>();
  }

  public void add(A delegate) {
    this.delegates.add(delegate);
  }

  @Override
  public Iterator<A> iterator() {
    return delegates.iterator();
  }

}
