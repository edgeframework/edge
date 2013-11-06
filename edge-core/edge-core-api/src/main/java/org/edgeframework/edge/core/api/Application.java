package org.edgeframework.edge.core.api;

public interface Application {
  public int getPort();

  public int port();

  public Application setPort(int port);

  public Application port(int port);

  public String getHost();

  public String host();

  public Application setHost(String host);

  public Application host(String host);
}
