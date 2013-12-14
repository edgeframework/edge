package org.edgeframework.edge.core._lang_.app;

import java.util.LinkedList;
import java.util.List;

import org.edgeframework.edge.core._lang_.filters.assets.Assets;
import org.edgeframework.edge.core._lang_.filters.mvc.Router;
import org.edgeframework.edge.core._lang_.http.Filter;
import org.edgeframework.edge.core._lang_.http.HttpContext;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;

public class Application {
  private Vertx vertx;

  private int port = 8080;
  private String host = "localhost";

  private List<ApplicationEventHandler> beforeStartHandlers = new LinkedList<>();
  private List<ApplicationEventHandler> afterStartHandlers = new LinkedList<>();
  private List<ApplicationEventHandler> beforeStopHandlers = new LinkedList<>();
  private List<ApplicationErrorEventHandler> onErrorHandlers = new LinkedList<>();

  public List<ApplicationEventHandler> getBeforeStartHandlers() {
    return this.beforeStartHandlers;
  }

  public List<ApplicationEventHandler> getAfterStartHandlers() {
    return this.afterStartHandlers;
  }

  public List<ApplicationEventHandler> getBeforeStopHandlers() {
    return this.beforeStopHandlers;
  }

  public List<ApplicationErrorEventHandler> getOnErrorHandlers() {
    return this.onErrorHandlers;
  }

  private List<Filter> requestFilters = new LinkedList<>();

  public void onRequest(Filter filter) {
    this.requestFilters.add(filter);
  }

  private Handler<HttpServerRequest> requestHandler = new Handler<HttpServerRequest>() {
    @Override
    public void handle(HttpServerRequest request) {
      new HttpContext(Application.this.vertx, request, Application.this.requestFilters);
    }
  };

  private Router router = new Router();

  public Router getRouter() {
    return this.router;
  }

  private Assets assets = new Assets("public");

  public Assets getAssets() {
    return this.assets;
  }

  public Application(Vertx vertx) {
    this.vertx = vertx;

    this.requestFilters.add(this.assets);
    // this.requestFilters.add(this.router);
  }

  /* Verticle Methods */
  public void start() {
    try {
      /* Lifecycle */
      this.beforeStart();

      /* Begin */
      startServer(this.vertx, this.port, this.host);
      this.afterStart();
    } catch (Throwable e) {
      this.onError(e);
    }
  }

  public void stop() {
    try {
      this.beforeStop();
    } catch (Throwable e) {
      this.onError(e);
    }
  }

  /* Event Handlers */
  public void beforeStart(ApplicationEventHandler handler) {
    this.beforeStartHandlers.add(handler);
  }

  public void afterStart(ApplicationEventHandler handler) {
    this.afterStartHandlers.add(handler);
  }

  public void beforeStop(ApplicationEventHandler handler) {
    this.beforeStopHandlers.add(handler);
  }

  public void onError(ApplicationErrorEventHandler handler) {
    this.onErrorHandlers.add(handler);
  }

  private void beforeStart() {
    for (ApplicationEventHandler h : beforeStartHandlers) {
      h.call(this);
    }
  }

  private void afterStart() {
    for (ApplicationEventHandler h : afterStartHandlers) {
      h.call(this);
    }
  }

  private void beforeStop() {
    for (ApplicationEventHandler h : beforeStopHandlers) {
      h.call(this);
    }
  }

  private void onError(Throwable e) {
    for (ApplicationErrorEventHandler h : onErrorHandlers) {
      h.call(this, e);
    }
  }

  /* Server Methods */
  private void startServer(Vertx vertx, int port, String host) {
    final HttpServer server = vertx.createHttpServer();
    server.requestHandler(this.requestHandler);
    server.listen(port, host);
  }

}
