package org.edgeframework.routing;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.vertx.java.core.json.JsonObject;

import com.github.jknack.handlebars.Handlebars;

public class HttpServerResponse {
  private HttpServerResponse that = this;

  private final org.vertx.java.core.http.HttpServerResponse response;

  public HttpServerResponse(org.vertx.java.core.http.HttpServerResponse response) {
    this.response = response;
  }

  /**
   * This sets the Http Response status value
   * 
   * @param value
   * @return
   */
  public HttpServerResponse status(int value) {
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
  public HttpServerResponse header(String header, Object value) {
    this.response.headers().put(header, value);
    return this;
  }

  public HttpServerResponse setContentType(String contentType) {
    this.header("Content-Type", contentType);
    return this;
  }

  /**
   * Renders a String to the response
   */
  public HttpServerResponse send(String content) {
    content = content != null ? content : "";

    if (!this.response.headers().containsKey("Content-Type")) {
      this.response.putHeader("Content-Type", "text/plain; charset=utf-8");
    }

    this.response.putHeader("Content-Length", content.length());
    this.response.end(content);
    return this;
  }

  public HttpServerResponse sendFile(Path path) {
    return this.sendFile(path.toString());
  }

  public HttpServerResponse sendFile(String path) {
    that.response.sendFile(path);
    return this;
  }

  /**
   * Renders a Template to the response
   */
  public HttpServerResponse render(String templateName) throws IOException {
    return this.render(templateName, (Map<String, Object>) null);
  }

  /**
   * Renders a Template to the response
   */
  public HttpServerResponse render(String templateName, Map<String, Object> data) throws IOException {
    this.response.putHeader("Content-Type", "text/html");

    Handlebars hb = new Handlebars();
    Path templatePath = Paths.get("templates", templateName + ".hbs");

    String contents = new String(Files.readAllBytes(templatePath));
    String compiled = hb.compile(contents).apply(data);

    this.response.end(compiled);

    return this;
  }

  public HttpServerResponse render(String templateName, JsonObject data) throws IOException {
    return render(templateName, data.toMap());
  }
}
