package org.edgeframework.edge.core.api.routing;

import java.util.regex.Pattern;

import org.edgeframework.edge.core.api.controllers.TypeConverter;
import org.edgeframework.edge.core.api.controllers.TypeConverterFunction;
import org.edgeframework.edge.core.api.http.Context;

public class DefaultRouter extends Router {
  /* ^.*\.(?<action>\w*\(.*\))$ */
  private Pattern ROUTE_PATTERN = Pattern.compile("^(?<controller>.*)\\.(?<action>\\w*\\(.*\\))$");

  private TypeConverter converter = new TypeConverter();

  public DefaultRouter register(String name, String pattern, Class<?> controllerClass) {
    throw new UnsupportedOperationException();
    // return this;
  }

  public <T> DefaultRouter convert(String parameter, Class<T> type, TypeConverterFunction<String, T> function) {
    converter.addConverter(parameter, type, function);
    return this;
  }

  @Override
  public void action(Context<?> context) {
    context.request().response().write("Hello World").close();
    context.next();
  }

}
