package org.edgeframework.core.faces;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.edgeframework.core.faces.Controller.Result;
import org.edgeframework.core.faces.Controller.TypeConverter;
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
  private Pattern PARAM_PATTERN = Pattern.compile("([^,:]+)(?::([^,:]+))?");

  private Map<String, Class<?>> paramTypes = new HashMap<>();
  private Map<Class<?>, TypeConverter> paramConversions = new HashMap<>();

  public ControllerFace(String name, String host, int port, String routesPath) {
    super(name, host, port);

    this.routesPath = routesPath;

    setupParameters();
  }

  private void setupParameters() {
    // default string types
    addConverter("", String.class, null);
    addConverter("string", String.class, null);

    addConverter("byte", byte.class, new TypeConverter() {
      @Override
      public Object call(String value) {
        return Byte.parseByte(value);
      }
    });

    addConverter("short", short.class, new TypeConverter() {
      @Override
      public Object call(String value) {
        return Short.parseShort(value);
      }
    });

    addConverter("int", int.class, new TypeConverter() {
      @Override
      public Object call(String value) {
        return Integer.parseInt(value);
      }
    });

    addConverter("long", long.class, new TypeConverter() {
      @Override
      public Object call(String value) {
        return Long.parseLong(value);
      }
    });

    addConverter("float", float.class, new TypeConverter() {
      @Override
      public Object call(String value) {
        return Float.parseFloat(value);
      }
    });

    addConverter("double", double.class, new TypeConverter() {
      @Override
      public Object call(String value) {
        return Double.parseDouble(value);
      }
    });

    addConverter("date", Date.class, new TypeConverter() {
      @Override
      public Object call(String value) {
        // attempt to parse long
        try {
          Long longResult = Long.parseLong(value);
          return new Date(longResult);
        } catch (NumberFormatException e) {
        }

        // okay so not a long, try to parse as a date string
        try {
          DateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");
          return formatter.parse(value);
        } catch (ParseException e) {
          throw new RuntimeException(e);
        }

      }
    });
  }

  @Override
  void configure(Yoke yoke) {
    yoke.use(router);
  }

  protected void register(final Class<? extends Controller> controller, final String method, final String pattern, final String action) throws Exception {
    // preserve insertion ordering
    final Map<String, Class<?>> captures = new LinkedHashMap<>();

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
      String paramType = paramMatcher.group(2);

      if (paramType != null) {
        paramType = paramType.trim().toLowerCase();
      } else {
        paramType = "";
      }

      captures.put(paramName, paramTypes.get(paramType));
    }

    final MethodHandle handle = MethodHandles
      .publicLookup()
      .findVirtual(controller, actionName, MethodType.methodType(Result.class, new ArrayList<>(captures.values())
        )
      );

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

          /* Attempt to coerce values into parameter types */
          for (Map.Entry<String, Class<?>> entry : captures.entrySet()) {
            String value = request.params().get(entry.getKey());
            TypeConverter converter = paramConversions.get(entry.getValue());

            if (converter != null) {
              args.add(converter.call(value));
            } else {
              args.add(value);
            }
          }

          /* Invoke Receiver, receive result */
          Result result = (Result) handle.invokeWithArguments(args);

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

  public void addConverter(String name, Class<?> type, TypeConverter converter) {
    this.paramTypes.put(name, type);
    this.paramConversions.put(type, converter);
  }
}
