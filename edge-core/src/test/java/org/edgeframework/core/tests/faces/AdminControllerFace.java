package org.edgeframework.core.tests.faces;

import org.edgeframework.core.faces.ControllerFace;

public class AdminControllerFace extends ControllerFace {
  public AdminControllerFace() throws Exception {
    super("Admin", "localhost", 8081, "");

    register(AdminController.class, "GET", "/index", "index()");
    register(AdminController.class, "GET", "/index/:query", "index(query:String)");
    register(AdminController.class, "GET", "/index/:query/:subquery", "index(query:String, subquery: String)");
    register(AdminController.class, "GET", "/index/:query/:subquery/:param", "index(query:String, subquery: String,param:String )");
  }
}
