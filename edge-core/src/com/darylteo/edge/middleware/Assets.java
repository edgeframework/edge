package com.darylteo.edge.middleware;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.vertx.java.deploy.impl.VertxLocator;

import com.darylteo.edge.core.requests.EdgeHandler;
import com.darylteo.edge.core.requests.EdgeRequest;
import com.darylteo.edge.core.requests.EdgeResponse;

public class Assets extends EdgeHandler {

  private Path path;

  public Assets(String path) {
    this.path = Paths.get(path);
  }

  @Override
  public void handleRequest(EdgeRequest request, EdgeResponse response) throws Exception {
    // Remove the first starting frontslash so that it isn't an absolute path
    String requestPath = request.getPath().substring(1);
    Path test = path.resolve(requestPath);
    File file = test.toFile();

    if (!file.isFile() || !file.exists()) {
      next();
    } else {
      VertxLocator.container.getLogger().debug("Delivering Asset: " + file.getAbsolutePath());      
      response.sendFile(test);
    }
  }

}
