package org.edgeframework.edge.core.api.http;

public interface Context<V> {
  public V vertx();

  public void next();

  public Request request();

  public Response response();
}
