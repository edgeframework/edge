package org.edgeframework.core.tests.faces;

import org.edgeframework.core.faces.ControllerFace;

public class AdminController extends ControllerFace {
  public AdminController() {
    super("Admin", "localhost", 8081);
  }

  public String index() {
    return "Index Page";
  }
}
