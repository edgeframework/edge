package org.edgeframework.routing;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.vertx.java.deploy.impl.VertxLocator;

class RouteDefinition {

  private static final Pattern validParamPattern = Pattern.compile("^:(?<paramname>[A-Za-z0-9]+)$");

  private final String method;
  private final List<RequestHandler> handlers;

  private Pattern pattern;
  private String[] paramIdentifiers;

  public RouteDefinition(String method, String stringPattern, RequestHandler... handlers) throws Exception {
    this.method = method;
    this.handlers = Arrays.asList(handlers);

    compilePattern(stringPattern);
  }

  public RouteDefinitionMatchResult matches(String method, String url) {
    if (!this.method.equals(method)) {
      return null;
    }

    Matcher matcher = pattern.matcher(url);
    if (!matcher.matches()) {
      return null;
    }

    Map<String, Object> params = new HashMap<>();
    for (String identifier : this.paramIdentifiers) {
      String value = matcher.group(identifier);
      params.put(identifier, value);
    }

    return new RouteDefinitionMatchResult(this, params);
  }

  public void addHandler(RequestHandler handler) {
    this.handlers.add(handler);
  }

  public RequestHandler[] getHandlers() {
    return this.handlers.toArray(new RequestHandler[0]);
  }

  private void compilePattern(String stringPattern) throws Exception {
    /* Catch All url */
    if (stringPattern.equals("*")) {
      this.pattern = Pattern.compile("^.*$");
      this.paramIdentifiers = new String[0];
      return;
    }

    if (!stringPattern.startsWith("/")) {
      throw new Exception("Route Pattern must start with '/'.");
    }

    /* Index url */
    if (stringPattern.equals("/")) {
      this.pattern = Pattern.compile("^/$");
      this.paramIdentifiers = new String[0];
      return;
    }

    /* Split the url pattern into parts */
    /* -1 parameter is provided to include empty strings */
    List<String> identifiers = new LinkedList<>();

    String[] parts = stringPattern.split("/", -1);
    StringBuilder completed = new StringBuilder("^");

    /* Start from 1 as the first part will always be empty */
    for (int i = 1; i < parts.length; i++) {
      completed.append("/");

      String part = parts[i];

      /* If the string pattern contains :param then validate the parameter name */
      if (part.startsWith(":")) {
        Matcher matcher = validParamPattern.matcher(part);
        if (!matcher.matches()) {
          throw new Exception("Invalid Param Name");
        }

        String identifier = matcher.group("paramname");

        completed.append(String.format("(?<%s>.+)", identifier));
        identifiers.add(identifier);
      } else {
        completed.append(part);
      }
    }

    completed.append("$");

    VertxLocator.container.getLogger().debug(String.format("String %s Compiled %s", stringPattern, completed));

    this.pattern = Pattern.compile(completed.toString());
    this.paramIdentifiers = identifiers.toArray(new String[0]);
  }
}
