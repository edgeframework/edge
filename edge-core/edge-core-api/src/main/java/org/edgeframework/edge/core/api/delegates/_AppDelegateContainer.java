package org.edgeframework.edge.core.api.delegates;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class _AppDelegateContainer<A, AD extends _AppDelegate<A>> implements Iterable<AD> {
  private List<AD> delegates = new LinkedList<>();

  public void add(AD delegate) {
    this.delegates.add(delegate);
  }

  @Override
  public Iterator<AD> iterator() {
    return delegates.iterator();
  }
}
