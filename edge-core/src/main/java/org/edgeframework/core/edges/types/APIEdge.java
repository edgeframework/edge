package org.edgeframework.core.edges.types;

import org.edgeframework.core.edges.AbstractEdge;

public abstract class APIEdge extends AbstractEdge {
  public APIEdge(String name, String host, int port) {
    super(name, host, port);
  }
}