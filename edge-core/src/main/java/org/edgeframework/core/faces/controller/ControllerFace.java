package org.edgeframework.core.faces.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.edgeframework.core.faces.AbstractFace;

import rx.util.functions.Func1;

import com.jetdrone.vertx.yoke.Yoke;
import com.jetdrone.vertx.yoke.middleware.Router;

public abstract class ControllerFace extends AbstractFace {
  private String routesPath = "";
  private Router router = new Router();

  /* ^.*\.(?<action>\w*\(.*\))$ */
  private Pattern ROUTE_PATTERN = Pattern.compile("^(?<controller>.*)\\.(?<action>\\w*\\(.*\\))$");

  private TypeConverter converter = new TypeConverter();

  private List<ActionMiddleware> handlers = new LinkedList<>(); // TODO: vertx
                                                                // hack

  public ControllerFace(String name, String host, int port, String routesPath) {
    super(name, host, port);

    this.routesPath = routesPath;
  }

  @Override
  public void configure(Yoke yoke) {
    for (ActionMiddleware handler : handlers) {
      handler.setVertx(vertx);
    }

    yoke.use(router);
  }

  protected void register(final Class<? extends Controller> controller, final String method, final String pattern, final String actionString) throws Exception {
    final RequestAction action = new RequestAction(controller, actionString, converter);
    ActionMiddleware handler = new ActionMiddleware(action);

    handlers.add(handler);
    router.all(pattern, handler);
  }

  protected void register(final String name, final Class<?> type, Func1<String, ?> converterFunction) {
    converter.addConverter(name, type, converterFunction);
  }

  public String getRoutesPath() {
    return routesPath;
  }

}
