package org.edgeframework.controllers.test;

import static org.vertx.testtools.VertxAssert.assertEquals;
import static org.vertx.testtools.VertxAssert.testComplete;

import org.edgeframework.controllers.Controllers;
import org.edgeframework.controllers.RouteControllerDefinition;
import org.edgeframework.testutils.TestHttpClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.json.JsonObject;
import org.vertx.testtools.TestVerticle;

import com.darylteo.rx.promises.PromiseAction;

public class ControllersTestClient extends TestVerticle {

  private static Logger logger = LoggerFactory
      .getLogger(ControllersTestClient.class);

  private TestHttpClient client;

  private static final String HOSTNAME = "localhost";
  private static final int PORT = 8081;

  private Controllers controllers;

  @Override
  public void start() {
    this.client = new TestHttpClient(vertx, HOSTNAME, PORT);

    try {
      this.controllers = new Controllers(vertx, HOSTNAME, PORT);
      displayInfo();
    } catch (Exception e) {
      e.printStackTrace();
    }

    super.start();
  }

  @Override
  public void stop() throws Exception {
    this.controllers = null;
    this.client.close();
    this.client = null;

    super.stop();
  }

  private void displayInfo() throws Exception {
    RouteControllerDefinition[] routes = controllers.getRoutes();

    for (RouteControllerDefinition def : routes) {
      logger.debug(String.format("%s %s %s\n", def.getMethod(), def.getRoute(), def.getController()));
    }
  }

  @Test
  public void testOkResult() throws Exception {
    this.client
        .getPage("/testOkResult")
        .then(new PromiseAction<String>() {
          @Override
          public void call(String value) {
            assertEquals("Page did not successfully load", value, "Hello World");
            testComplete();
          }
        });
  }

  @Test
  public void testRenderResult() throws Exception {
    this.client
        .getPage("/testRenderResult")
        .then(new PromiseAction<String>() {
          @Override
          public void call(String value) {
            assertEquals("Page was not rendered correctly", value, "Hello World");
            testComplete();
          }
        });
  }

  @Test
  public void testJsonResult() throws Exception {
    this.client
        .getPage("/testJsonResult")
        .then(new PromiseAction<String>() {
          @Override
          public void call(String value) {
            JsonObject json = new JsonObject(value);

            assertEquals("Json Data not rendered properly", json.getString("echo"), "Hello World");
            testComplete();
          }
        });
  }

  @Test
  public void testAsyncResult() throws Exception {
    this.client
        .getPage("/testAsyncResult")
        .then(new PromiseAction<String>() {
          @Override
          public void call(String value) {
            assertEquals("Asynchronus result not rendered properly", value, "Hello World");
            testComplete();
          }
        });
  }

  @Test
  public void testRouteParams1() throws Exception {
    this.client
        .getPage("/testRouteParams/HelloWorld")
        .then(new PromiseAction<String>() {

          @Override
          public void call(String value) {
            assertEquals("Did not manage to pass through the route parameter", value, "HelloWorld");
            testComplete();
          }

        });
  }

  @Test
  public void testQueryString1() throws Exception {
    this.client
        .getPage("/testQueryString?echo=HelloWorld")
        .then(new PromiseAction<String>() {
          @Override
          public void call(String value) {
            assertEquals("Did not manage to pass through the query parameter", value, "HelloWorld");
            testComplete();
          }

        });
  }

  @Test
  public void testBodyString1() throws Exception {
    this.client
        .postPage("/testBodyString", "echo=HelloWorld")
        .then(new PromiseAction<String>() {

          @Override
          public void call(String value) {
            assertEquals("Did not manage to pass through the body data", value, "HelloWorld");
            testComplete();
          }
        });
  }
}