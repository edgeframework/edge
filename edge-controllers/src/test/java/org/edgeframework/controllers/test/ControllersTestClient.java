package org.edgeframework.controllers.test;

import org.edgeframework.controllers.Controllers;
import org.edgeframework.controllers.RouteControllerDefinition;
import org.edgeframework.core.util.org.edgeframework.routing.test.TestHttpClient;
import org.edgeframework.promises.PromiseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.testframework.TestClientBase;

public class ControllersTestClient extends TestClientBase {

  private static Logger logger = LoggerFactory.getLogger(ControllersTestClient.class);

  private TestHttpClient client;

  private static final String HOSTNAME = "localhost";
  private static final int PORT = 8081;

  private Controllers controllers;

  @Override
  public void start() {
    super.start();

    try {
      this.controllers = new Controllers(vertx, HOSTNAME, PORT);
      this.client = new TestHttpClient(vertx, HOSTNAME, PORT);

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
      logger.debug(String.format("%s %s %s\n", def.getMethod(), def.getRoute(), def.getController()));
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
        .getPage("/testRouteParams/HelloWorld")
        .then(new PromiseHandler<String, Void>() {

          @Override
          public Void handle(String value) {
            tu.azzert(value.equals("HelloWorld"), "Did not manage to pass through the route parameter");

            tu.testComplete();
            return null;
          }

        });
  }

  public void testQueryString1() throws Exception {
    this.client
        .getPage("/testQueryString?echo=HelloWorld")
        .then(new PromiseHandler<String, Void>() {

          @Override
          public Void handle(String value) {
            tu.azzert(value.equals("HelloWorld"), "Did not manage to pass through the route parameter");

            tu.testComplete();
            return null;
          }

        });
  }

  public void testBodyString1() throws Exception {
    this.client
        .postPage("/testBodyString", "echo=HelloWorld")
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