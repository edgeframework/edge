package org.edgeframework.edge.core.api;

import org.edgeframework.edge.core.api.delegates.DelegatesContainer;

public interface Application<A extends Application<A>> extends DelegatesContainer<A> {
  public int getPort();

  public int port();

  public A setPort(int port);

  public A port(int port);

  public String getHost();

  public String host();

  public A setHost(String host);

  public A host(String host);
}
