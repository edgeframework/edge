package org.edgeframework.core.tests.faces;

import org.edgeframework.core.faces.ControllerFace;

public class AdminControllerFace extends ControllerFace {
  public AdminControllerFace() throws Exception {
    super("Admin", "localhost", 8081, "");

    register(AdminController.class, "GET", "/echo", "echo()");

    // Default type
    register(AdminController.class, "GET", "/mapping/:p1", "mapping(p1)");
    register(AdminController.class, "GET", "/mapping/:p1/:p2", "mapping(p1:String, p2: String)");
    register(AdminController.class, "GET", "/mapping/:p1/:p2/:p3", "mapping(p1:String, p2: String,p3:String )");

    register(AdminController.class, "GET", "/types/byte/:value", "datatype(value: byte)");
    register(AdminController.class, "GET", "/types/short/:value", "datatype(value: short)");
    register(AdminController.class, "GET", "/types/int/:value", "datatype(value: int)");
    register(AdminController.class, "GET", "/types/long/:value", "datatype(value: long)");
    register(AdminController.class, "GET", "/types/float/:value", "datatype(value: float)");
    register(AdminController.class, "GET", "/types/double/:value", "datatype(value: double)");
  }
}
