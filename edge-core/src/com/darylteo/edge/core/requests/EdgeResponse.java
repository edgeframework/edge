package com.darylteo.edge.core.requests;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.vertx.java.core.http.HttpServerResponse;

import com.github.jknack.handlebars.Handlebars;

public class EdgeResponse {
  private final HttpServerResponse response;

  public EdgeResponse(HttpServerResponse response) {
    this.response = response;
  }

  /**
   * This sets the Http Response status value
   * 
   * @param value
   * @return
   */
  public EdgeResponse status(int value) {
    this.response.statusCode = value;
    return this;
  }

  /**
   * Sets a Http Response Header
   * 
   * @param header
   * @param value
   * @return
   */
  public EdgeResponse header(String header, Object value) {
    this.response.headers().put(header, value);
    return this;
  }

  /**
   * Renders a String to the response
   */
  public EdgeResponse renderHtml(String html) {
    this.response.putHeader("Content-Type", "text/html");
    this.response.end(html);
    return this;
  }

  /**
   * Renders a Template to the response
   */
  public EdgeResponse renderTemplate(String templateName) throws IOException {
    return this.renderTemplate(templateName, null);
  }

  /**
   * Renders a Template to the response
   */
  public EdgeResponse renderTemplate(String templateName, Object data) throws IOException {
    this.response.putHeader("Content-Type", "text/html");

    Handlebars hb = new Handlebars();
    Path templatePath = Paths.get("edge-examples", "templates", templateName + ".hbs");

    String contents = new String(Files.readAllBytes(templatePath));
    String compiled = hb.compile(contents).apply(data);

    this.response.end(compiled);

    return this;
  }
}
