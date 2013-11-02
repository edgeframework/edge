package org.edgeframework.edge.core.api;

public interface Edge<V> {
  /* Lifecycle Hooks */
  public void beforeStart();

  public void afterStart();

  public void beforeStop();

  public void onError(Throwable error);
  
  public V vertx();
}
