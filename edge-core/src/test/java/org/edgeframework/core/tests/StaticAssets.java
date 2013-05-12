package org.edgeframework.core.tests;

import org.edgeframework.core.faces.StaticFace;

public class StaticAssets extends StaticFace {
  public StaticAssets() {
    super("Static Assets");

    this.setBasePath("assets");
  }

}