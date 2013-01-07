package org.edgeframework.controller;

import org.edgeframework.routing.handler.EdgeResponse;

public abstract class Result {

  private String contentType = null;

  public Result as(String contentType) {
    this.contentType = contentType;
    return this;
  }

  void performResult(EdgeResponse response) throws Exception {
    this.perform(response);
    if (this.contentType != null) {
      response.setContentType(this.contentType);
    }
  }

  protected abstract void perform(EdgeResponse response) throws Exception;
}
