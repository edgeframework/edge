package org.edgeframework.core.faces;

import com.jetdrone.vertx.yoke.Yoke;
import com.jetdrone.vertx.yoke.middleware.Static;

public abstract class StaticFace extends AbstractFace {
  private String basePath = "";

  public StaticFace(String name, String host, int port) {
    super(name, host, port);
  }

  @Override
  void configure(Yoke yoke) {
    yoke.use(new Static(basePath));
  }

  public void setBasePath(String basePath) {
    this.basePath = basePath;
  }

  public String getBasePath() {
    return this.basePath;
  }

}
