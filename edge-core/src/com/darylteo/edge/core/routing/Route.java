package com.darylteo.edge.core.routing;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.vertx.java.core.Handler;
import org.vertx.java.deploy.impl.VertxLocator;

import com.darylteo.edge.core.EdgeRequest;

public class Route {
  private final String method;
  private final Pattern pattern;
  private final Handler<EdgeRequest> handler;

  public Route(String method, String stringPattern, Handler<EdgeRequest> handler) {
    this.method = method;
    this.handler = handler;

    this.pattern = compilePattern(stringPattern);
  }

  public boolean matches(String method, String url) {
    if (!this.method.equals(method)) {
      return false;
    }

    Matcher matcher = pattern.matcher(url);
    if (!matcher.matches()) {
      return false;
    }

    return true;
  }

  public Handler<EdgeRequest> getHandler() {
    return this.handler;
  }

  private Pattern compilePattern(String stringPattern) {
    if (stringPattern.equals("/")) {
      VertxLocator.container.getLogger().info(String.format("String %s Compiled %s", stringPattern, "^/$"));
      return Pattern.compile("^/$");
    }

    /* Split the url pattern into parts */
    String[] parts = stringPattern.split("/");
    StringBuilder completed = new StringBuilder("^");

    for (int i = 0; i < parts.length; i++) {
      String part = parts[i];

      part = part.replace("*", ".*");

      if (i > 0) {
        completed.append("/");
      }
      completed.append(part);
    }

    completed.append("$");

    VertxLocator.container.getLogger().info(String.format("String %s Compiled %s", stringPattern, completed));

    return Pattern.compile(completed.toString());
  }
}
