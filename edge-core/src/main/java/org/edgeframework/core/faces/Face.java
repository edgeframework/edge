package org.edgeframework.core.faces;

import org.vertx.java.platform.Verticle;

public abstract class Face extends Verticle {
  private String name;

  public Face(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
