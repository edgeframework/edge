package org.edgeframework.edge.core.api;

/**
 * Indicates a class from which a instance of Vertx can be retrieved publicly.
 * @author Daryl Teo
 *
 * @param <V> the language specific Vertx class.
 */
public interface VertxContainer<V> {
  public V getVertx();

  public V vertx();
}
