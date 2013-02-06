package org.edgeframework.test;

import org.edgeframework.controller.Controllers;
import org.edgeframework.controller.RouteControllerDefinition;
import org.edgeframework.promises.PromiseHandler;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.deploy.impl.VertxLocator;
import org.vertx.java.testframework.TestClientBase;

public class ControllersTestClient extends TestClientBase {

  private TestHttpClient client;

  private static final String HOSTNAME = "localhost";
  private static final int PORT = 8081;

  private Controllers controllers;

  @Override
  public void start() {
    super.start();

    try {
      HttpServer server = VertxLocator.vertx.createHttpServer();

      this.controllers = new Controllers("routes.config", server);
      this.client = new TestHttpClient(HOSTNAME, PORT);
      server.listen(PORT, HOSTNAME);

      displayInfo();
    } catch (Exception e) {
      e.printStackTrace();
    }

    tu.appReady();
  }

  @Override
  public void stop() {
    this.client.close();
    tu.appStopped();

    super.stop();
  }

  private void displayInfo() throws Exception {
    RouteControllerDefinition[] routes = controllers.getRoutes();

    for (RouteControllerDefinition def : routes) {
      System.out.printf("%s %s %s\n", def.getMethod(), def.getRoute(), def.getController());
    }
  }

  public void testOkResult() throws Exception {
    this.client
        .getPage("/testOkResult")
        .then(new PromiseHandler<String, Void>() {
          @Override
          public Void handle(String value) {
            tu.azzert(value.equals("Hello World"));
            tu.testComplete();

            return null;
          }
        });
  }

  public void testRenderResult() throws Exception {
    this.client
        .getPage("/testRenderResult")
        .then(new PromiseHandler<String, Void>() {
          @Override
          public Void handle(String value) {
            tu.azzert(value.equals("Hello World"));
            tu.testComplete();

            return null;
          }
        });
  }

  public void testJsonResult() throws Exception {
    this.client
        .getPage("/testJsonResult")
        .then(new PromiseHandler<String, Void>() {
          @Override
          public Void handle(String value) {
            JsonObject json = new JsonObject(value);
            tu.azzert(json.getString("echo").equals("Hello World"));
            tu.testComplete();

            return null;
          }
        });
  }

  public void testAsyncResult() throws Exception {
    this.client
        .getPage("/testAsyncResult")
        .then(new PromiseHandler<String, Void>() {
          @Override
          public Void handle(String value) {
            tu.azzert(value.equals("Hello World"));
            tu.testComplete();

            return null;
          }
        });
  }

  public void testRouteParams1() throws Exception {
    this.client
        .getPage("/params/HelloWorld")
        .then(new PromiseHandler<String, Void>() {

          @Override
          public Void handle(String value) {
            tu.azzert(value.equals("HelloWorld"), "Did not manage to pass through the route parameter");

            tu.testComplete();
            return null;
          }

        });
  }

}