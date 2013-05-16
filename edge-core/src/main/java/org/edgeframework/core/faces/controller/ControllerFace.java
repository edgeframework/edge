package org.edgeframework.core.faces.controller;

import java.util.regex.Pattern;

import org.edgeframework.core.faces.AbstractFace;

import com.jetdrone.vertx.yoke.Middleware;
import com.jetdrone.vertx.yoke.Yoke;
import com.jetdrone.vertx.yoke.middleware.Router;

public abstract class ControllerFace extends AbstractFace {
  private String routesPath = "";
  private Router router = new Router();

  /* ^.*\.(?<action>\w*\(.*\))$ */
  private Pattern ROUTE_PATTERN = Pattern.compile("^(?<controller>.*)\\.(?<action>\\w*\\(.*\\))$");

  private TypeConverter converter = new TypeConverter();

  public ControllerFace(String name, String host, int port, String routesPath) {
    super(name, host, port);

    this.routesPath = routesPath;
  }

  @Override
  public void configure(Yoke yoke) {
    yoke.use(router);
  }

  protected void register(final Class<? extends Controller> controller, final String method, final String pattern, final String actionString) throws Exception {
    final RequestAction action = new RequestAction(controller, actionString, converter);
    Middleware handler = new ActionMiddleware(action);

    router.all(pattern, handler);
  }

  public String getRoutesPath() {
    return routesPath;
  }

}
