package org.edgeframework.core.tests.faces;

import java.util.UUID;

import org.edgeframework.java.core.controller.ControllerEdge;

import rx.util.functions.Func1;

public class AdminControllerEdge extends ControllerEdge {
  public AdminControllerEdge() throws Exception {
    super("Admin", "localhost", 8081, "");
  }

  @Override
  public void beforeStart() {
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

    register(AdminController.class, "GET", "/sessions/", "sessionsPage()");
    register(AdminController.class, "GET", "/sessions/end", "sessionsEnd()");
    register(AdminController.class, "GET", "/sessions/:value", "sessionsPage(value: string)");
    register(AdminController.class, "GET", "/cookies/", "cookiesPage()");
    register(AdminController.class, "GET", "/cookies/:value", "cookiesPage(value: string)");

    register(AdminController.class, "GET", "/types/fallthrough/:value", "fallthrough(value: int)");
    register(AdminController.class, "GET", "/types/fallthrough/:value", "fallthrough(value: string)");
  }

}
