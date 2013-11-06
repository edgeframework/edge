package org.edgeframework.edge.core.java;

import org.edgeframework.edge.core.java.delegates.AppDelegateContainer;
import org.edgeframework.edge.core.java.filters.FilterContainer;

public interface Application {
  public int getPort();

  public int port();

  public Application setPort(int port);

  public Application port(int port);

  public String getHost();

  public String host();

  public Application setHost(String host);

  public Application host(String host);

  public AppDelegateContainer delegates();

  public AppDelegateContainer getDelegates();

  public FilterContainer filters();

  public FilterContainer getFilters();
}
