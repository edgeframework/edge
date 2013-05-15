package org.edgeframework.core.tests.faces;

import java.util.Date;

import org.edgeframework.core.faces.Controller;

public class AdminController extends Controller {
  public AdminController() {
  }

  public Result echo() {
    String echo = "index";
    if (request().params().contains("get")) {
      echo = request().params().get("get");
    }

    return ok(echo);
  }

  public Result mapping(String query) {
    return ok(query);
  }

  public Result mapping(String query, String subquery) {
    return ok(query + ":" + subquery);
  }

  public Result mapping(String query, String subquery, String test) {
    return ok(query + ":" + subquery + ":" + test);
  }

  public Result datatype(byte value) {
    return ok("" + value);
  }

  public Result datatype(short value) {
    return ok("" + value);
  }

  public Result datatype(int value) {
    return ok("" + value);
  }

  public Result datatype(long value) {
    return ok("" + value);
  }

  public Result datatype(float value) {
    return ok(String.format("%f.2", value));
  }

  public Result datatype(double value) {
    return ok(String.format("%f.2", value));
  }

  public Result datatype(Date value) {
    return ok(value.toString());
  }
}