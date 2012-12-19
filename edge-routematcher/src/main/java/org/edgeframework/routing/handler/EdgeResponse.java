package org.edgeframework.routing.handler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.vertx.java.core.http.HttpServerResponse;
import org.vertx.java.core.json.JsonObject;

import com.github.jknack.handlebars.Handlebars;

public class EdgeResponse {
  private EdgeResponse that = this;
  
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
  public EdgeResponse send(String content) {
    this.response.putHeader("Content-Type", "text/html");
    this.response.end(content);
    return this;
  }
  
  public EdgeResponse sendFile(Path path) {
    return this.sendFile(path.toString());
  }
  
  public EdgeResponse sendFile(String path) {
      that.response.sendFile(path);
      return this;
  }

  /**
   * Renders a Template to the response
   */
  public EdgeResponse render(String templateName) throws IOException {
    return this.render(templateName, (Map<String, Object>) null);
  }

  /**
   * Renders a Template to the response
   */
  public EdgeResponse render(String templateName, Map<String, Object> data) throws IOException {
    this.response.putHeader("Content-Type", "text/html");

    Handlebars hb = new Handlebars();
    Path templatePath = Paths.get("templates", templateName + ".hbs");

    String contents = new String(Files.readAllBytes(templatePath));
    String compiled = hb.compile(contents).apply(data);

    this.response.end(compiled);

    return this;
  }

  public EdgeResponse render(String templateName, JsonObject data) throws IOException {
    return render(templateName, data.toMap());
  }
}
