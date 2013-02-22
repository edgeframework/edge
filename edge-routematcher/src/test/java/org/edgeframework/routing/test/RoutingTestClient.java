package org.edgeframework.routing.test;

import static org.vertx.testtools.VertxAssert.assertEquals;
import static org.vertx.testtools.VertxAssert.fail;
import static org.vertx.testtools.VertxAssert.testComplete;

import org.edgeframework.core.util.org.edgeframework.routing.test.TestHttpClient;
import org.edgeframework.promises.FailureHandler;
import org.edgeframework.promises.PromiseHandler;
import org.edgeframework.routing.HttpServerRequest;
import org.edgeframework.routing.HttpServerResponse;
import org.edgeframework.routing.RouteMatcher;
import org.edgeframework.routing.handler.ParamHandler;
import org.edgeframework.routing.handler.RequestHandler;
import org.edgeframework.routing.middleware.Assets;
import org.edgeframework.routing.middleware.BodyParser;
import org.junit.Test;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.json.JsonObject;
import org.vertx.testtools.TestVerticle;

public class RoutingTestClient extends TestVerticle {

  private TestHttpClient client;

  private static final String HOSTNAME = "localhost";
  private static final int PORT = 8080;

  @Override
  public void start() {
    this.client = new TestHttpClient(vertx, HOSTNAME, PORT);
    createApplication();

    super.start();
  }

  @Override
  public void stop() throws Exception {
    this.client.close();

    super.stop();
  }

  private void createApplication() {
    RouteMatcher routematcher = new RouteMatcher();

    routematcher
        /* Index */
        .get("/basic-test", new RequestHandler() {
          @Override
          public void handle(HttpServerRequest request, HttpServerResponse response) throws Exception {
            response.render("basic", new JsonObject("{\"echo\":\"Hello World\"}"));
          }
        })

        .get("/multiple-handlers-test", new RequestHandler() {
          @Override
          public void handle(HttpServerRequest request, HttpServerResponse response) {
            request.getData().put("echo", "Hello World");
            next();
          }
        }, new RequestHandler() {
          @Override
          public void handle(HttpServerRequest request, HttpServerResponse response) throws Exception {
            JsonObject context = new JsonObject()
                .putString("echo", (String) request.getData().get("echo"));

            response.render("basic", context);
          }
        })

        .get("/multiple-handlers-test2", new RequestHandler() {
          @Override
          public void handle(HttpServerRequest request, HttpServerResponse response) {
            // NOOP
            response.send("done");
            testComplete();
          }
        }, new RequestHandler() {
          @Override
          public void handle(HttpServerRequest request, HttpServerResponse response) throws Exception {
            fail("Should not be calling this handler!");
            testComplete();
          }
        })

        .get("/param-test1/:echo", new RequestHandler() {
          @Override
          public void handle(HttpServerRequest request, HttpServerResponse response) throws Exception {

            String echo = (String) request.getParams().get("echo");

            // Does not exist, funnel through
            if (echo.equals("does-not-exist")) {
              next();
            } else {

              JsonObject context = new JsonObject()
                  .putString("echo", echo);

              response.render("basic", context);
            }

          }
        })

        // Param test
        .get("/param-test2/:param", new RequestHandler() {
          @Override
          public void handle(HttpServerRequest request, HttpServerResponse response) throws Exception {
            JsonObject context = new JsonObject()
                .putString("echo", (String) request.getData().get("data"));

            response.render("basic", context);
          }
        })
        .param("param", new ParamHandler<String>() {
          @Override
          public void handle(HttpServerRequest request, HttpServerResponse response, String value) {
            request.getData().put("data", value.toUpperCase());
            next();
          }
        })

        .post("/post-test", new RequestHandler() {
          @Override
          public void handle(HttpServerRequest request, HttpServerResponse response) throws Exception {
            JsonObject context = new JsonObject()
                .putString("echo", (String) request.getBody().get("data"));

            response.render("basic", context);
          }
        })

        .all("*", new RequestHandler() {

          @Override
          public void handle(HttpServerRequest request, HttpServerResponse response) throws Exception {
            response.status(404);
            response.send("");
          }

        })

        .use(new Assets("public"))
        .use(new BodyParser());

    HttpServer server = vertx.createHttpServer();
    server.requestHandler(routematcher);
    server.listen(PORT, HOSTNAME);
  }

  @Test
  public void testBasicRoute1() throws Exception {
    this.client
        .getPageStatus("/basic-test")
        .then(new PromiseHandler<Integer, Void>() {

          @Override
          public Void handle(Integer value) {
            assertEquals("Value not retrieved", value.intValue(), 200);

            testComplete();
            return null;
          }

        });
  }

  @Test
  public void testBasicRoute2() throws Exception {
    this.client
        .getPage("/basic-test")
        .then(new PromiseHandler<String, Void>() {

          @Override
          public Void handle(String value) {
            assertEquals("Value not retrieved", "Hello World", value);

            testComplete();
            return null;
          }

        });
  }

  @Test
  public void testMultipleHandlers1() throws Exception {
    this.client
        .getPage("/multiple-handlers-test")
        .then(new PromiseHandler<String, Void>() {

          @Override
          public Void handle(String value) {
            assertEquals("Did not successfully call multiple handlers", "Hello World", value);

            testComplete();
            return null;
          }

        });
  }

  @Test
  public void testMultipleHandlers2() throws Exception {
    this.client.getPage("/multiple-handlers-test2");
  }

  @Test
  public void testPost() throws Exception {
    this.client
        .postPage("/post-test", "data=Hello%20World")
        .then(
            new PromiseHandler<String, Void>() {

              @Override
              public Void handle(String value) {
                assertEquals("Post data not passed", "Hello World", value);

                testComplete();
                return null;
              }

            },
            new FailureHandler<Void>() {

              @Override
              public Void handle(Exception e) {
                e.printStackTrace();
                fail(e.getMessage());
                testComplete();
                return null;
              }
            }
        );
  }

  @Test
  public void test404_1() throws Exception {
    this.client
        .getPageStatus("/does-not-exist")
        .then(new PromiseHandler<Integer, Void>() {

          @Override
          public Void handle(Integer value) {
            assertEquals("404 error not given", value.intValue(), 404);

            testComplete();
            return null;
          }

        });
  }

  @Test
  public void test404_2() throws Exception {
    this.client
        .postPageStatus("/does-not-exist", "Hello World")
        .then(new PromiseHandler<Integer, Void>() {

          @Override
          public Void handle(Integer value) {
            assertEquals("404 error not given", value.intValue(), 404);

            testComplete();
            return null;
          }

        });
  }

  @Test
  public void test404_3() throws Exception {
    this.client
        .getPageStatus("echo/does-not-exist")
        .then(new PromiseHandler<Integer, Void>() {

          @Override
          public Void handle(Integer value) {
            assertEquals("404 error not given", value.intValue(), 404);

            testComplete();
            return null;
          }

        });
  }

  @Test
  public void testRouteParams1() throws Exception {
    this.client
        .getPage("/param-test1/HelloWorld")
        .then(new PromiseHandler<String, Void>() {

          @Override
          public Void handle(String value) {
            assertEquals("Route Param not filled", "HelloWorld", value);

            testComplete();
            return null;
          }

        });
  }

  @Test
  public void testRouteParams2() throws Exception {
    this.client
        .getPage("/param-test2/HelloWorld")
        .then(new PromiseHandler<String, Void>() {

          @Override
          public Void handle(String value) {
            assertEquals(value, "HELLOWORLD", value);

            testComplete();
            return null;
          }

        });
  }
}
