package org.edgeframework.core.tests.faces;

import java.util.Date;
import java.util.UUID;

import org.edgeframework.java.core.controller.Controller;

public class AdminController extends Controller {
  public AdminController() {
  }

  public Result echo() {
    String echo = "index";
    if (request().getParams().contains("get")) {
      echo = request().getParams().get("get");
    }

    return ok(echo);
  }

  /* Mapping Tests */
  public Result mapping(String query) {
    return ok(query);
  }

  public Result mapping(String query, String subquery) {
    return ok(query + ":" + subquery);
  }

  public Result mapping(String query, String subquery, String test) {
    return ok(query + ":" + subquery + ":" + test);
  }

  /* Datattype Tests */
  public Result datatype(byte value) {
    return ok(value + ":byte");
  }

  public Result datatype(short value) {
    return ok(value + ":short");
  }

  public Result datatype(int value) {
    return ok(value + ":int");
  }

  public Result datatype(long value) {
    return ok(value + ":long");
  }

  public Result datatype(float value) {
    return ok(String.format("%.2f:float", value));
  }

  public Result datatype(double value) {
    return ok(String.format("%.2f:double", value));
  }

  public Result datatype(Date value) {
    return ok(value.toString() + ":date");
  }

  public Result datatype(UUID value) {
    return ok(value.toString() + ":uuid");
  }

  /* Sessions */
  public Result sessionsPage(String value) {
    beginSession();
    session().put("key", value);

    return ok("sessions:" + value);
  }

  public Result sessionsEnd() {
    endSession();
    return ok("sessions:end");
  }

  public Result sessionsPage() {
    if (isActiveSession()) {
      String value = (String) session().get("key");
      return ok("sessions:" + value);
    } else {
      return ok("Not Logged In");
    }
  }

  /* Cookies */
  public Result cookiesPage(String value) {
    cookies("cookie", value);
    return ok("cookie:" + value);
  }

  public Result cookiesPage() {
    String value = cookies("cookie");
    return ok("cookie:" + value);
  }

  /* Fall through Test */
  public Result fallthrough(int value) {
    return ok(value + ":int");
  }

  public Result fallthrough(String value) {
    return ok(value + ":string");
  }
}