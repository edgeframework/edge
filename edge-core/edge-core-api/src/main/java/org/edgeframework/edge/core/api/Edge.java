package org.edgeframework.edge.core.api;

public interface Edge<V> {
  /* Lifecycle Hooks */
  public void beforeStart();

  public void afterStart();

  public void beforeStop();

  public void onError(Throwable error);

  /* Configuration */
  public int port();

  public Edge<V> port(int port);

  public String host();

  public Edge<V> host(String host);

  /* Accessors */
  public V vertx();
}
