package org.edgeframework.routing.test;

import org.edgeframework.promises.FailureHandler;
import org.edgeframework.promises.PromiseHandler;
import org.edgeframework.routing.RequestHandler;
import org.edgeframework.routing.HttpServerRequest;
import org.edgeframework.routing.HttpServerResponse;
import org.edgeframework.routing.RouteMatcher;
import org.edgeframework.routing.middleware.Assets;
import org.edgeframework.routing.middleware.BodyParser;
import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.testframework.TestClientBase;

public class RoutingTestClient extends TestClientBase {

  private TestHttpClient client;

  private static final String HOSTNAME = "localhost";
  private static final int PORT = 8080;

  @Override
  public void start() {
    super.start();

    this.client = new TestHttpClient(HOSTNAME, PORT);
    createApplication();

    tu.appReady();
  }

  @Override
  public void stop() {
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
          }
        }, new RequestHandler() {
          @Override
          public void handle(HttpServerRequest request, HttpServerResponse response) throws Exception {
            tu.azzert(false, "Should not be calling this handler!");
            tu.testComplete();
          }
        })

        .get("/params/:echo", new RequestHandler() {
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

        .post("/post-test", new RequestHandler() {
          @Override
          public void handle(HttpServerRequest request, HttpServerResponse response) throws Exception {
            System.out.println("Hello");
            JsonObject context = new JsonObject()
                .putString("echo", (String) request.getBody().get("data"));

            System.out.println(context.encode());
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

  public void testBasicRoute1() throws Exception {
    this.client
        .getPageStatus("/basic-test")
        .then(new PromiseHandler<Integer, Void>() {

          @Override
          public Void handle(Integer value) {
            tu.azzert(value.equals(200), "Failed to get correct HTTP Status");

            tu.testComplete();
            return null;
          }

        });
  }

  public void testBasicRoute2() throws Exception {
    this.client
        .getPage("/basic-test")
        .then(new PromiseHandler<String, Void>() {

          @Override
          public Void handle(String value) {
            tu.azzert(value.equals("Hello World"), "Did not manage to load the correct handlebars template");

            tu.testComplete();
            return null;
          }

        });
  }

  public void testMultipleHandlers1() throws Exception {
    this.client
        .getPage("/multiple-handlers-test")
        .then(new PromiseHandler<String, Void>() {

          @Override
          public Void handle(String value) {
            tu.azzert(value.equals("Hello World"), "Did not manage to load the correct handlebars template");

            tu.testComplete();
            return null;
          }

        });
  }

  public void testMultipleHandlers2() throws Exception {
    this.client.getPage("/multiple-handlers-test2");
  }

  public void testPost() throws Exception {
    this.client
        .postPage("/post-test", "data=Hello%20World")
        .then(
            new PromiseHandler<String, Void>() {

              @Override
              public Void handle(String value) {
                System.out.println(value);
                tu.azzert(value.equals("Hello World"), "Did not manage to POST data.");

                tu.testComplete();
                return null;
              }

            },
            new FailureHandler<Void>() {

              @Override
              public Void handle(Exception e) {
                e.printStackTrace();
                tu.azzert(false, "Exception");
                return null;
              }
            }
        );
  }

  public void test404_1() throws Exception {
    this.client
        .getPageStatus("/does-not-exist")
        .then(new PromiseHandler<Integer, Void>() {

          @Override
          public Void handle(Integer value) {
            tu.azzert(value.equals(404), "Failed to get correct HTTP Status");

            tu.testComplete();
            return null;
          }

        });
  }

  public void test404_2() throws Exception {
    this.client
        .postPageStatus("/does-not-exist", "Hello World")
        .then(new PromiseHandler<Integer, Void>() {

          @Override
          public Void handle(Integer value) {
            tu.azzert(value.equals(404), "Failed to get correct HTTP Status");

            tu.testComplete();
            return null;
          }

        });
  }

  public void test404_3() throws Exception {
    this.client
        .getPageStatus("echo/does-not-exist")
        .then(new PromiseHandler<Integer, Void>() {

          @Override
          public Void handle(Integer value) {
            tu.azzert(value.equals(404), "Failed to get correct HTTP Status");

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
