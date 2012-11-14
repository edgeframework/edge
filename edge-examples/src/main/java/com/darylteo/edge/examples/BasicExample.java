package com.darylteo.edge.examples;

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
      public void handleRequest(EdgeRequest request, EdgeResponse response) {
        System.out.println("Ha!");
      }
    }, new EdgeHandler() {
      @Override
      public void handleRequest(EdgeRequest request, EdgeResponse response) {
        response.renderText("This is the index page");
      }
    })

    // /* Static Path - give a query and it'll print out the query */
    // .get("/info", new EdgeHandler() {
    //
    // @Override
    // public void handleRequest(EdgeRequest request, EdgeResponse response) {
    // response.renderText(request.getQuery().toString());
    // }
    // })
    //
    // /* Static Path - give a query and it'll print out the query */
    // .get("/info/:param", new EdgeHandler() {
    //
    // @Override
    // public void handleRequest(EdgeRequest request, EdgeResponse response) {
    // response.renderText(request.getQuery().toString());
    // }
    // })
    //
    // .get("*", new EdgeHandler() {
    //
    // @Override
    // public void handleRequest(EdgeRequest request, EdgeResponse response) {
    // response.status(404).renderText("File Not Found");
    // }
    // })

        .listen(8080, "localhost");

  }
}
