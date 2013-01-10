package org.edgeframework.controller;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.edgeframework.routing.Routing;
import org.edgeframework.routing.handler.EdgeHandler;
import org.edgeframework.routing.handler.EdgeRequest;
import org.edgeframework.routing.handler.EdgeResponse;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.deploy.impl.VertxLocator;

public class Controllers {
  private static final String COMMENT_PREFIX = "#";
  private static final Pattern ROUTE_REGEX = Pattern.compile("^(?<method>\\S+)\\s+(?<route>\\S+)\\s+(?<controller>.+)$");

  private Routing routeMatcher = new Routing();

  public Controllers(String file, HttpServer server) throws Exception {
    try (
        Scanner s = new Scanner(
            new String(Files.readAllBytes(Paths.get(file)))
            )) {

      while (s.hasNextLine()) {
        String line = s.nextLine().trim();

        if (line.startsWith(COMMENT_PREFIX)) {
          continue;
        }

        Matcher matcher = ROUTE_REGEX.matcher(line);
        if (!matcher.matches()) {
          VertxLocator.container.getLogger().warn("Controller Route File contains invalid line: " + line);
          continue;
        }

        String method = matcher.group("method");
        String route = matcher.group("route");
        String controller = matcher.group("controller");

        this.addRoute(method, route, controller);
      }
    } catch (Exception e) {
      throw new Exception("Could not load the route configuration file", e);
    }

    server.requestHandler(routeMatcher);
  }

  private final List<RouteControllerDefinition> definitions = new LinkedList<>();

  private void addRoute(String method, String route, String controller) throws Exception {
    final RouteControllerDefinition definition = new RouteControllerDefinition(method, route, controller);
    this.definitions.add(definition);

    this.routeMatcher.addRoute(method, route, new EdgeHandler() {

      @Override
      public void handleRequest(EdgeRequest request, EdgeResponse response) throws Exception {
        Result result = definition.invoke(request.getParams());

        result.performResult(response);
      }

    });
  }

  public RouteControllerDefinition[] getRoutes() {
    return definitions.toArray(new RouteControllerDefinition[definitions.size()]);
  }
}
