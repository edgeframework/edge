package org.edgeframework.core.tests.faces;

import org.edgeframework.core.faces.ControllerFace;

public class AdminControllerFace extends ControllerFace {
  public AdminControllerFace() throws Exception {
    super("Admin", "localhost", 8081, "");

    register("GET", "/index", AdminController.class, "index");
    register("GET", "/index2", AdminController.class, "index");
  }
}
