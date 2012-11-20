package com.darylteo.edge.examples;

import java.util.HashMap;
import java.util.Map;

import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.deploy.Verticle;

import com.darylteo.edge.core.EdgeApplication;
import com.darylteo.edge.core.requests.EdgeHandler;
import com.darylteo.edge.core.requests.EdgeRequest;
import com.darylteo.edge.core.requests.EdgeResponse;
import com.github.jknack.handlebars.Context;

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
                        .putString("name", "Server Error Example")
                        .putString("url", "/examples/exception")
                    )
                );

            response.renderTemplate("index", Context.newContext(context.toMap()));
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
            String param = (String) request.getData().get("pass");
            response.renderTemplate("basic", param);
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
            response.renderTemplate("post", request.getBody());
          }
        })

        .get("/exception", new EdgeHandler() {

          @Override
          public void handleRequest(EdgeRequest request, EdgeResponse response) {
            String[] array = new String[0];
            array[1] = "Throw!";
          }

        })

        .use(EdgeApplication.bodyParser)

        .listen(8080, "localhost");

  }
}
