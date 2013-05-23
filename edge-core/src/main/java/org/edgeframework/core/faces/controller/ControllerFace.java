package org.edgeframework.core.faces.controller;

import java.util.regex.Pattern;

import javax.crypto.Mac;

import org.edgeframework.core.faces.AbstractFace;

import rx.util.functions.Func1;

import com.jetdrone.vertx.yoke.Yoke;
import com.jetdrone.vertx.yoke.middleware.CookieParser;
import com.jetdrone.vertx.yoke.middleware.Router;
import com.jetdrone.vertx.yoke.middleware.Session;
import com.jetdrone.vertx.yoke.util.Utils;

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
    final Mac hmac = Utils.newHmacSHA256("keyboard.cat");

    yoke.use(new CookieParser(hmac));
    yoke.use(new Session("edge:session", "/", false, 60 * 60 * 1000, hmac));
    yoke.use(router);
  }

  protected void register(final Class<? extends Controller> controller, final String method, final String pattern, final String actionString) throws Exception {
    final RequestAction action = new RequestAction(controller, actionString, converter);
    ActionMiddleware handler = new ActionMiddleware(action);

    router.all(pattern, handler);
  }

  protected void register(final String name, final Class<?> type, Func1<String, ?> converterFunction) throws Exception {
    converter.addConverter(name, type, converterFunction);
  }

  public String getRoutesPath() {
    return routesPath;
  }

}
