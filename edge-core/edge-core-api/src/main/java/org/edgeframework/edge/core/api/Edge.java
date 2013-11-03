package org.edgeframework.edge.core.api;

import java.util.List;

import org.edgeframework.edge.core.api.filters.Filter;

public interface Edge<V> {
  /* Lifecycle Hooks */
  public void beforeStart();

  public void afterStart();

  public void beforeStop();

  public void onError(Throwable error);

  /* Properties */
  public List<? extends Filter> filters();

  /* Accessors */
  public V vertx();
}
