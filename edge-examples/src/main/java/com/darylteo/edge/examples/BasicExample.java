package com.darylteo.edge.examples;

import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.deploy.Verticle;

import com.darylteo.edge.core.EdgeApplication;
import com.darylteo.edge.core.requests.EdgeHandler;
import com.darylteo.edge.core.requests.EdgeRequest;
import com.darylteo.edge.core.requests.EdgeResponse;

// run com.darylteo.edge.examples.BasicExample -cp bin;

public class BasicExample extends Verticle {

  @Override
  public void start() throws Exception {
    EdgeApplication edge = new EdgeApplication();

    edge

        /* Index */
        .get("/", new EdgeHandler() {
          @Override
          public void handleRequest(EdgeRequest request, EdgeResponse response) throws Exception {
            JsonObject context = new JsonObject()
                .putArray("links", new JsonArray()
                    .addObject(new JsonObject()
                        .putString("name", "Basic Example")
                        .putString("url", "/examples/basic")
                    )
                    .addObject(new JsonObject()
                        .putString("name", "Basic Example (Multiple Handlers)")
                        .putString("url", "/examples/multiple")
                    )
                    .addObject(new JsonObject()
                        .putString("name", "POST Example")
                        .putString("url", "/examples/post")
                    )
                    .addObject(new JsonObject()
                        .putString("name", "Server Error (500)")
                        .putString("url", "/examples/exception")
                    )
                    .addObject(new JsonObject()
                        .putString("name", "File Not Found (404)")
                        .putString("url", "/examples/random")
                    )
                );

            response.renderTemplate("index", context);
          }
        })

        .get("/examples/basic", new EdgeHandler() {
          @Override
          public void handleRequest(EdgeRequest request, EdgeResponse response) throws Exception {
            response.renderTemplate("basic");
          }
        })

        .get("/examples/multiple", new EdgeHandler() {
          @Override
          public void handleRequest(EdgeRequest request, EdgeResponse response) {
            request.getData().put("pass", "through");
            next();
          }
        }, new EdgeHandler() {
          @Override
          public void handleRequest(EdgeRequest request, EdgeResponse response) throws Exception {
            JsonObject context = new JsonObject()
                .putString("pass", (String) request.getData().get("pass"));

            response.renderTemplate("basic", context);
          }
        })

        .get("/examples/post", new EdgeHandler() {
          @Override
          public void handleRequest(EdgeRequest request, EdgeResponse response) throws Exception {
            response.renderTemplate("post");
          }
        })
        .post("/examples/post", new EdgeHandler() {
          @Override
          public void handleRequest(EdgeRequest request, EdgeResponse response) throws Exception {
            JsonObject context = new JsonObject()
                .putObject("body", new JsonObject(request.getBody()));

            response.renderTemplate("post", context);
          }
        })

        .get("/examples/exception", new EdgeHandler() {

          @Override
          public void handleRequest(EdgeRequest request, EdgeResponse response) {
            Object obj = null;
            obj.toString();
          }

        })

        .all("*", new EdgeHandler() {
          @Override
          public void handleRequest(EdgeRequest request, EdgeResponse response) throws Exception {
            /* 404 */
            response
                .status(404)
                .renderTemplate("404");
          }
        })

        .use(EdgeApplication.bodyParser)

        .listen(8080, "localhost");

  }
}
