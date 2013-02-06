package org.edgeframework.core.test;

import org.edgeframework.core.EdgeApplication;
import org.edgeframework.promises.FailureHandler;
import org.edgeframework.promises.PromiseHandler;
import org.edgeframework.routing.handler.EdgeHandler;
import org.edgeframework.routing.handler.EdgeRequest;
import org.edgeframework.routing.handler.EdgeResponse;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.testframework.TestClientBase;

public class ApplicationTestClient extends TestClientBase {

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
    EdgeApplication edge = new EdgeApplication();

    edge
        /* Index */
        .get("/basic-test", new EdgeHandler() {
          @Override
          public void handleRequest(EdgeRequest request, EdgeResponse response) throws Exception {
            response.render("basic", new JsonObject("{\"echo\":\"Hello World\"}"));
          }
        })

        .get("/multiple-handlers-test", new EdgeHandler() {
          @Override
          public void handleRequest(EdgeRequest request, EdgeResponse response) {
            request.getData().put("echo", "Hello World");
            next();
          }
        }, new EdgeHandler() {
          @Override
          public void handleRequest(EdgeRequest request, EdgeResponse response) throws Exception {
            JsonObject context = new JsonObject()
                .putString("echo", (String) request.getData().get("echo"));

            response.render("basic", context);
          }
        })

        .get("/params/:echo", new EdgeHandler() {
          @Override
          public void handleRequest(EdgeRequest request, EdgeResponse response) throws Exception {
            JsonObject context = new JsonObject()
                .putString("echo", (String) request.getParams().get("echo"));

            response.render("basic", context);
          }
        })

        .post("/post-test", new EdgeHandler() {
          @Override
          public void handleRequest(EdgeRequest request, EdgeResponse response) throws Exception {
            JsonObject context = new JsonObject()
                .putString("echo", (String) request.getBody().get("data"));

            response.render("basic", context);
          }
        })

        .all("*", new EdgeHandler() {

          @Override
          public void handleRequest(EdgeRequest request, EdgeResponse response) throws Exception {
            response.status(404);
            response.send("");
          }

        })

        .use(EdgeApplication.assets("public"))
        .use(EdgeApplication.bodyParser())

        .listen(PORT, HOSTNAME);
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

  public void testMultipleHandlers() throws Exception {
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

  public void testPost() throws Exception {
    this.client
        .postPage("/post-test", "data=Hello%20World")
        .then(
            new PromiseHandler<String, Void>() {

              @Override
              public Void handle(String value) {
                tu.azzert(value.equals("Hello World"), "Did not manage to POST data.");

                tu.testComplete();
                return null;
              }

            },
            new FailureHandler<Void>() {

              @Override
              public Void handle(Throwable e) {
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
