package org.edgeframework.java.core;

import java.util.regex.Pattern;

import org.edgeframework.java.core.controller.TypeConverter;
import org.edgeframework.java.core.routing.Router;
import org.vertx.java.platform.Verticle;

public abstract class Edge extends Verticle {
  private String routesPath = "";
  private Router router = new Router();

  /* ^.*\.(?<action>\w*\(.*\))$ */
  private Pattern ROUTE_PATTERN = Pattern.compile("^(?<controller>.*)\\.(?<action>\\w*\\(.*\\))$");

  private TypeConverter converter = new TypeConverter();
}
