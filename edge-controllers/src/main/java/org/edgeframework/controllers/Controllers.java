package org.edgeframework.controllers;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.edgeframework.routing.HttpServerRequest;
import org.edgeframework.routing.HttpServerResponse;
import org.edgeframework.routing.RouteMatcher;
import org.edgeframework.routing.handler.RequestHandler;
import org.edgeframework.routing.middleware.Assets;
import org.edgeframework.routing.middleware.BodyParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.http.HttpServer;

public class Controllers {
  private static final Logger logger = LoggerFactory.getLogger(Controllers.class);

  private static final String COMMENT_PREFIX = "#";
  private static final Pattern ROUTE_REGEX = Pattern.compile("^(?<method>\\S+)\\s+(?<route>\\S+)\\s+(?<controller>.+)$");

  private final Vertx vertx;

  private final HttpServer server;

  private RouteMatcher routeMatcher;

  public Controllers(Vertx vertx, String host, int port) throws Exception {
    this(vertx, "routes.config", host, port);
  }

  public Controllers(Vertx vertx, String file, String host, int port) throws Exception {
    this.vertx = vertx;

    try (
        // This is only done once on init, so don't really care about async
        Scanner s = new Scanner(
            new String(Files.readAllBytes(Paths.get(file)))
            )) {

      // Used to store routes
      this.routeMatcher = new RouteMatcher();

      while (s.hasNextLine()) {
        String line = s.nextLine().trim();

        if (line.startsWith(COMMENT_PREFIX)) {
          continue;
        }

        Matcher matcher = ROUTE_REGEX.matcher(line);
        if (!matcher.matches()) {
          // TODO: Logging
          continue;
        }

        String method = matcher.group("method");
        String route = matcher.group("route");
        String controller = matcher.group("controller");

        this.addRoute(method, route, controller);
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw new Exception("Could not load the route configuration file", e);
    }

    this.routeMatcher.use(new Assets("public"));
    this.routeMatcher.use(new BodyParser());

    this.server = vertx.createHttpServer();
    this.server.requestHandler(this.routeMatcher);
    this.server.listen(port, host);
  }

  private final List<RouteControllerDefinition> definitions = new LinkedList<>();

  private void addRoute(String method, String route, String controller) throws Exception {
    final RouteControllerDefinition definition = new RouteControllerDefinition(method, route, controller);
    this.definitions.add(definition);

    this.routeMatcher.route(method, route, new RequestHandler() {

      @Override
      public void handle(HttpServerRequest request, HttpServerResponse response) throws Exception {
        Result result = definition.invoke(vertx, request);

        result.performResult(response);
      }

    });
  }

  public RouteControllerDefinition[] getRoutes() {
    return definitions.toArray(new RouteControllerDefinition[definitions.size()]);
  }
}
