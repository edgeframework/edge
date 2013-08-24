package org.edgeframework.core.tests.faces;

import org.edgeframework.core.faces.impl.StaticEdge;

public class StaticAssets extends StaticEdge {
  public StaticAssets() {
    super("Static Assets", "localhost", 8080, "assets");
  }
}