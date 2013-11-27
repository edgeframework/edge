package org.edgeframework.edge.core._internal.delegates;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class AppDelegateContainerInternal<A> implements Iterable<A> {
  private List<A> delegates;

  public AppDelegateContainerInternal() {
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
