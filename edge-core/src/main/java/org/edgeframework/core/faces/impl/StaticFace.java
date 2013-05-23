package org.edgeframework.core.faces.impl;

import com.jetdrone.vertx.yoke.Yoke;
import com.jetdrone.vertx.yoke.middleware.Static;

public abstract class StaticFace extends AbstractFace {
  private String basePath = "";

  public StaticFace(String name, String host, int port, String basePath) {
    super(name, host, port);
    this.basePath = basePath;
  }

  @Override
  void configure(Yoke yoke) {
    yoke.use(new Static(basePath));
  }

  public String getBasePath() {
    return this.basePath;
  }

}
