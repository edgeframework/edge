package org.edgeframework.routing.middleware;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.edgeframework.routing.HttpServerRequest;
import org.edgeframework.routing.HttpServerResponse;
import org.edgeframework.routing.handler.RequestHandler;

public class Assets extends RequestHandler {

  private Path path;

  public Assets(String path) {
    this.path = Paths.get(path);
  }

  @Override
  public void handle(HttpServerRequest request, HttpServerResponse response) throws Exception {
    // Remove the first starting frontslash so that it isn't an absolute path
    String requestPath = request.getPath().substring(1);
    Path test = path.resolve(requestPath);
    File file = test.toFile();

    if (!file.isFile() || !file.exists()) {
      next();
    } else {
      // TODO: getLogger().debug("Delivering Asset: " + file.getAbsolutePath());
      response.sendFile(test);
    }
  }

}
