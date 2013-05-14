package org.edgeframework.core.tests.faces;

import org.edgeframework.core.faces.StaticFace;

public class StaticAssets extends StaticFace {
  public StaticAssets() {
    super("Static Assets", "localhost", 8080);

    this.setBasePath("assets");
  }
}