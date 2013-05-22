package org.edgeframework.core.faces.controller;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.edgeframework.core.faces.controller.Controller.Result;
import org.vertx.java.core.MultiMap;
import org.vertx.java.core.Vertx;

import rx.util.functions.Func1;

import com.jetdrone.vertx.yoke.middleware.YokeRequest;

/**
 * Used to hold details of an Action e.g. index(value:int)
 * 
 * @author dteo
 * 
 */
class RequestAction {
  /* ^.*\.(?<action>\w*\())$ */
  private static final Pattern ACTION_PATTERN = Pattern.compile("^(?<name>.+)\\((?<params>.*)\\)$");
  private static final Pattern PARAM_PATTERN = Pattern.compile("([^,:]+)(?::([^,:]+))?");

  /* Controller */
  private final Class<? extends Controller> controller;
  private final MethodHandle handle;

  /* Action Properties */
  private final String name;
  private final LinkedHashMap<String, Func1<String, ?>> captures = new LinkedHashMap<>();

  public RequestAction(Class<? extends Controller> controller, String actionString, TypeConverter converter) throws Exception {
    this.controller = controller;

    // attempt to match
    Matcher matcher = ACTION_PATTERN.matcher(actionString);
    if (!matcher.matches()) {
      throw new Exception("Could not parse action");
    }

    this.name = matcher.group("name");
    String paramString = matcher.group("params");
    MethodType mt = MethodType.methodType(Result.class);

    // parse parameters
    Matcher paramMatcher = PARAM_PATTERN.matcher(paramString);
    while (paramMatcher.find()) {
      String paramName = paramMatcher.group(1).trim();
      String paramType = paramMatcher.group(2);

      paramType = paramType != null ? paramType.trim().toLowerCase() : "string";

      Class<?> type = converter.toType(paramType);
      mt = mt.appendParameterTypes(type);

      this.captures.put(paramName, converter.getConverter(type));
    }

    this.handle = MethodHandles
      .publicLookup()
      .findVirtual(controller, this.name, mt);
  }

  public void invoke(Vertx vertx, YokeRequest request) throws Throwable {
    /* Setup Receiver */
    Controller receiver = this.controller.newInstance();
    receiver.setRequest(request);
    receiver.setVertx(vertx);

    /* Get Param Values */
    List<Object> args = coerceParams(request.params());

    /* Invoke Receiver, receive result */
    args.add(0, receiver);
    Result result = (Result) this.handle.invokeWithArguments(args);

    /* Render result to response */
    result.render(request.response());
  }

  private List<Object> coerceParams(MultiMap params) {
    List<Object> result = new LinkedList<>();

    for (Map.Entry<String, Func1<String, ?>> entry : this.captures.entrySet()) {
      String value = params.get(entry.getKey());
      result.add(entry.getValue().call(value));
    }

    return result;
  }
}