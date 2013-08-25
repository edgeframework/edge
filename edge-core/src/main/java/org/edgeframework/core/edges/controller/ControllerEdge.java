package org.edgeframework.core.edges.controller;

import java.util.regex.Pattern;

import javax.crypto.Mac;

import org.edgeframework.core.edges.AbstractEdge;
import org.vertx.java.core.Handler;

import rx.util.functions.Func1;

import com.jetdrone.vertx.yoke.Middleware;
import com.jetdrone.vertx.yoke.Yoke;
import com.jetdrone.vertx.yoke.middleware.CookieParser;
import com.jetdrone.vertx.yoke.middleware.Router;
import com.jetdrone.vertx.yoke.middleware.Session;
import com.jetdrone.vertx.yoke.middleware.YokeRequest;
import com.jetdrone.vertx.yoke.util.Utils;

public abstract class ControllerEdge extends AbstractEdge {
  private String routesPath = "";
  private Router router = new Router();

  private Mac hmac = Utils.newHmacSHA256("keyboard.cat");

  /* ^.*\.(?<action>\w*\(.*\))$ */
  private Pattern ROUTE_PATTERN = Pattern.compile("^(?<controller>.*)\\.(?<action>\\w*\\(.*\\))$");

  private TypeConverter converter = new TypeConverter();

  public ControllerEdge(String name, String host, int port, String routesPath) {
    super(name, host, port);

    this.routesPath = routesPath;
  }

  @Override
  protected void configure(Yoke yoke) {
    yoke.use(new CookieParser(hmac));
    yoke.use(new Session("edge:session", "/", false, 60 * 60 * 1000, hmac));
    yoke.use(router);
  }

  protected void register(final Class<? extends Controller> controller, final String method, final String pattern, final String actionString) {
    try {
      final RequestAction action = new RequestAction(controller, actionString, converter);
      final Middleware handler = new Middleware() {
        @Override
        public void handle(YokeRequest request, Handler<Object> next) {
          try {
            action.invoke(vertx, request, ControllerEdge.this);
          } catch (Throwable e) {
            e.printStackTrace();
            next.handle(e);
          }
        }
      };

      router.all(pattern, handler);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  protected void register(final String name, final Class<?> type, Func1<String, ?> converterFunction) {
    converter.addConverter(name, type, converterFunction);
  }

  public String getRoutesPath() {
    return routesPath;
  }

  public Mac getMac() {
    return this.hmac;
  }

}
