package org.edgeframework.core.tests.faces;

import java.util.UUID;

import org.edgeframework.core.faces.controller.ControllerFace;

import rx.util.functions.Func1;

public class AdminControllerFace extends ControllerFace {
  public AdminControllerFace() throws Exception {
    super("Admin", "localhost", 8081, "");

    register("uuid", UUID.class, new Func1<String, UUID>() {
      @Override
      public UUID call(String name) {
        return UUID.fromString(name);
      }
    });

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
    register(AdminController.class, "GET", "/types/timestamp/:value", "datatype(value: date)");
    register(AdminController.class, "GET", "/types/date/:value", "datatype(value: date)");
    register(AdminController.class, "GET", "/types/uuid/:value", "datatype(value: uuid)");

    register(AdminController.class, "GET", "/sessions/", "sessions()");
    register(AdminController.class, "GET", "/sessions/:value", "sessions(value: string)");
    register(AdminController.class, "GET", "/cookies/", "cookies()");
    register(AdminController.class, "GET", "/cookies/:value", "cookies(value: string)");

    register(AdminController.class, "GET", "/types/fallthrough/:value", "fallthrough(value: int)");
    register(AdminController.class, "GET", "/types/fallthrough/:value", "fallthrough(value: string)");
  }
}
