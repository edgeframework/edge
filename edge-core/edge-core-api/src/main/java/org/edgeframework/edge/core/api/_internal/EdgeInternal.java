package org.edgeframework.edge.core.api._internal;


public interface EdgeInternal {
  /* Lifecycle */
  public void __configure();

  public void __begin();

  public void __defaults(int port, String host);
}
