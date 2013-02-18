package org.edgeframework.controllers;

import org.edgeframework.routing.HttpServerResponse;

public abstract class Result {

  private String contentType = null;

  public Result as(String contentType) {
    this.contentType = contentType;
    return this;
  }

  void performResult(HttpServerResponse response) throws Exception {
    this.perform(response);
    if (this.contentType != null) {
      response.setContentType(this.contentType);
    }
  }

  protected abstract void perform(HttpServerResponse response) throws Exception;
}
