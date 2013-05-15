package org.edgeframework.core.tests.faces;

import org.edgeframework.core.faces.Controller;

public class AdminController extends Controller {
  public AdminController() {
  }

  public Result index() {
    if (request().params().contains("query")) {
      return ok(request().params().get("query"));
    }

    return ok("index");
  }
}