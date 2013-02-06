package org.edgeframework.core;

import java.util.LinkedList;
import java.util.List;

import org.edgeframework.routing.handler.EdgeHandler;
import org.edgeframework.routing.handler.EdgeRequest;
import org.edgeframework.routing.handler.EdgeResponse;
import org.edgeframework.routing.middleware.Assets;
import org.edgeframework.routing.middleware.BodyParser;
import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeFunction;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.vertx.java.deploy.impl.VertxLocator;


public class EdgeApplicationJS {

  private EdgeApplication app;

  public EdgeApplicationJS() {
    this.app = new EdgeApplication();
  }

  /* Server Functions */
  public EdgeApplicationJS get(String urlPattern, NativeFunction... handlers) {
    app.get(urlPattern, wrapFunctions(handlers));
    return this;
  }

  public EdgeApplicationJS post(String urlPattern, NativeFunction... handlers) {
    app.post(urlPattern, wrapFunctions(handlers));
    return this;
  }

  public EdgeApplicationJS put(String urlPattern, NativeFunction... handlers) {
    app.put(urlPattern, wrapFunctions(handlers));
    return this;
  }

  public EdgeApplicationJS delete(String urlPattern, NativeFunction... handlers) {
    app.delete(urlPattern, wrapFunctions(handlers));
    return this;
  }

  public EdgeApplicationJS all(String urlPattern, NativeFunction... handlers) {
    EdgeHandler[] wrapped = wrapFunctions(handlers);
    app.get(urlPattern, wrapped)
        .post(urlPattern, wrapped)
        .put(urlPattern, wrapped)
        .delete(urlPattern, wrapped);

    return this;
  }

  public EdgeApplicationJS listen(int port) {
    app.listen(port);
    return this;
  }

  public EdgeApplicationJS listen(int port, String hostname) {
    app.listen(port, hostname);
    return this;
  }

  /* Middleware */
  private static final EdgeHandler bodyParser = new BodyParser();
  
  public static EdgeHandler bodyParser(){
    return EdgeApplication.bodyParser;
  }
  
  public static EdgeHandler assets(String path){
    return new Assets(path);
  }
  
  public EdgeApplicationJS use(Object... handlers) {
    for (Object handler : handlers) {
      if (handler instanceof NativeFunction) {
        app.use(wrapFunction((NativeFunction) handler));
      } else if (handler instanceof EdgeHandler) {
        app.use((EdgeHandler) handler);
      } else {
        VertxLocator.container.getLogger().warn("Invalid Parameter for EdgeApplication.use(): Not a function handler");
      }
    }

    return this;
  }

  /* NativeFunction wrappers */
  private EdgeHandler wrapFunction(NativeFunction function) {
    return new JSHandler(function);
  }

  private EdgeHandler[] wrapFunctions(NativeFunction[] functions) {
    List<EdgeHandler> handlers = new LinkedList<>();

    for (NativeFunction function : functions) {
      handlers.add(wrapFunction(function));
    }

    return handlers.toArray(new EdgeHandler[0]);
  }

  private class JSHandler extends EdgeHandler {

    private final NativeFunction function;

    public JSHandler(NativeFunction function) {
      this.function = function;
    }

    @Override
    public void handleRequest(EdgeRequest request, EdgeResponse response) throws Exception {
      Context cx = Context.enter();
      try {
        ScriptableObject scope = (ScriptableObject) cx.initStandardObjects();

        function.call(
            cx,
            scope,
            scope,
            new Object[] {
                request,
                response,
                new BaseFunction() {

                  @Override
                  public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
                    next();
                    return null;
                  }

                  @Override
                  public int getArity() {
                    return 0;
                  }

                }
            }
            );
      } finally {
        Context.exit();
      }
    }
  }
}
