package org.edgeframework.edge.core._lang_.delegates;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class AppDelegateContainer implements Iterable<AppDelegate> {
  private List<AppDelegate> delegates;

  public AppDelegateContainer() {
    delegates = new LinkedList<>();
  }

  public void add(AppDelegate delegate) {
    this.delegates.add(delegate);
  }

  @Override
  public Iterator<AppDelegate> iterator() {
    return delegates.iterator();
  }
}
