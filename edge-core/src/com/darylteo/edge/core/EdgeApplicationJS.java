package com.darylteo.edge.core;

import java.util.LinkedList;
import java.util.List;

import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeFunction;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import com.darylteo.edge.core.requests.EdgeHandler;
import com.darylteo.edge.core.requests.EdgeRequest;
import com.darylteo.edge.core.requests.EdgeResponse;

public class EdgeApplicationJS {

  private EdgeApplication app;

  public final EdgeHandler bodyParser = EdgeApplication.bodyParser;

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

  public EdgeApplicationJS listen(int port) {
    app.listen(port);
    return this;
  }

  public EdgeApplicationJS listen(int port, String hostname) {
    app.listen(port, hostname);
    return this;
  }

  /* Middleware */
  public EdgeApplicationJS use(Object... handlers) {
    List<EdgeHandler> middleware = new LinkedList<>();

    for (Object handler : handlers) {
      if (handler instanceof NativeFunction) {
        middleware.add(wrapFunction((NativeFunction) handler));
      } else if (handler instanceof EdgeHandler) {
        middleware.add((EdgeHandler) handler);
      } else {
        throw new IllegalArgumentException("Invalid Parameter for use: Not a function handler");
      }
    }

    app.use(middleware.toArray(new EdgeHandler[0]));
    return this;
  }

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
