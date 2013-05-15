package org.edgeframework.core.faces;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.vertx.java.core.Handler;

import com.jetdrone.vertx.yoke.Yoke;
import com.jetdrone.vertx.yoke.middleware.Router;
import com.jetdrone.vertx.yoke.middleware.YokeRequest;
import com.jetdrone.vertx.yoke.middleware.YokeResponse;

public abstract class ControllerFace extends AbstractFace {
  private String routesPath = "";
  private Router router = new Router();

  /* ^.*\.(?<action>\w*\(.*\))$ */
  private Pattern ROUTE_PATTERN = Pattern.compile("^(?<controller>.*)\\.(?<action>\\w*\\(.*\\))$");
  /* ^.*\.(?<action>\w*\())$ */
  private Pattern ACTION_PATTERN = Pattern.compile("^(?<name>.+)\\((?<params>.*)\\)$");
  private Pattern PARAM_PATTERN = Pattern.compile("([^,:]+):([^,:]+)");

  private Map<String, Class<?>> paramTypes = new HashMap<>();

  public ControllerFace(String name, String host, int port, String routesPath) {
    super(name, host, port);

    this.routesPath = routesPath;

    paramTypes.put("", String.class);
    paramTypes.put("string", String.class);
    paramTypes.put("int", Integer.class);
    paramTypes.put("long", Long.class);
    paramTypes.put("byte", Byte.class);
    paramTypes.put("short", Short.class);
    paramTypes.put("double", Double.class);
    paramTypes.put("float", Float.class);
    paramTypes.put("boolean", Boolean.class);
  }

  @Override
  void configure(Yoke yoke) {
    yoke.use(router);
  }

  protected void register(final Class<? extends Controller> controller, final String method, final String pattern, final String action) throws Exception {
    MethodType params = MethodType.methodType(Controller.Result.class);
    final List<String> captures = new LinkedList<>();

    // attempt to match
    Matcher matcher = ACTION_PATTERN.matcher(action);
    if (!matcher.matches()) {
      // TODO: recover from bad formatted script
    }

    final String actionName = matcher.group("name");
    final String paramString = matcher.group("params");

    Matcher paramMatcher = PARAM_PATTERN.matcher(paramString);
    while (paramMatcher.find()) {
      String paramName = paramMatcher.group(1).trim();
      String paramType = paramMatcher.group(2).trim().toLowerCase();

      params = params.appendParameterTypes(paramTypes.get(paramType));
      captures.add(paramName);
    }

    final MethodHandle handle = MethodHandles.publicLookup().findVirtual(controller, actionName, params);

    Handler<YokeRequest> handler = new Handler<YokeRequest>() {
      @Override
      public void handle(YokeRequest request) {
        YokeResponse response = request.response();
        try {
          Controller receiver = controller.newInstance();

          /* Setup Receiver */
          receiver.setRequest(request);

          /* Get Param Values */
          List<Object> args = new LinkedList<>();
          args.add(receiver);
          for (String paramName : captures) {
            args.add(request.params().get(paramName));
          }

          /* Invoke Receiver, receive result */
          Controller.Result result = (Controller.Result) handle.invokeWithArguments(args);

          /* Render result to response */
          result.render(response);
        } catch (Throwable e) {
          response.setStatusCode(500);
          response.setStatusMessage(e.getMessage());
          e.printStackTrace();
        }
      }
    };

    router.all(pattern, handler);
  }

  public String getRoutesPath() {
    return routesPath;
  }

  protected abstract class RouteDefinition {
    private String method;
    private Class<?> controller;
    private String action;
  }
}
