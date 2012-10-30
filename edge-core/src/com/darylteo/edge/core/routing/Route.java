package com.darylteo.edge.core.routing;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.vertx.java.core.Handler;

import com.darylteo.edge.core.EdgeRequest;

public class Route {
  private final String method;
  private final Pattern pattern;
  private final Handler<EdgeRequest> handler;

  public Route(String method, Pattern pattern, Handler<EdgeRequest> handler) {
    this.method = method;
    this.pattern = pattern;
    this.handler = handler;
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
}
