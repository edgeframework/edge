package org.edgeframework.edge.core._lang_.filters.mvc;

import org.edgeframework.edge.core._lang_.http.Context;

public class Controller {
  private Context context;

  public Context getContext() {
    return this.context;
  }

  public ActionResult ok(final String message) {
    return new ActionResult() {
      @Override
      public void action(Context context) {
        context.getResponse().header("content-length", message.length()).write(message).close();
      }
    };
  }

  public ActionResult error() {
    return new ActionResult() {
      @Override
      public void action(Context context) {
        context.getResponse().statusCode(500).close();
      }
    };
  }
}