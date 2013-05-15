package org.edgeframework.core.tests.faces;

import org.edgeframework.core.faces.Controller;

public class AdminController extends Controller {
  public AdminController() {
  }

  public Result index() {
    String echo = "index";
    if (request().params().contains("get")) {
      echo = request().params().get("get");
    }

    return ok(echo);
  }

  public Result index(String query) {
    return ok(query);
  }

  public Result index(String query, String subquery) {
    return ok(query + ":" + subquery);
  }

  public Result index(String query, String subquery, String test) {
    return ok(query + ":" + subquery + ":" + test);
  }
}