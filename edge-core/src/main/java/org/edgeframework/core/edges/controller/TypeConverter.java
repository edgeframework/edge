package org.edgeframework.core.edges.controller;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import rx.util.functions.Func1;

class TypeConverter {
  private Map<String, Class<?>> namesToType = new HashMap<>();
  private Map<Class<?>, Func1<String, ? extends Object>> typeToConverter = new HashMap<>();

  public TypeConverter() {
    // default string types
    addConverter("string", String.class, new Func1<String, Object>() {
      @Override
      public Object call(String value) {
        return value;
      }
    });

    // default primitives
    addConverter("byte", byte.class, new Func1<String, Object>() {
      @Override
      public Object call(String value) {
        return Byte.parseByte(value);
      }
    });

    addConverter("short", short.class, new Func1<String, Object>() {
      @Override
      public Object call(String value) {
        return Short.parseShort(value);
      }
    });

    addConverter("int", int.class, new Func1<String, Object>() {
      @Override
      public Object call(String value) {
        return Integer.parseInt(value);
      }
    });

    addConverter("long", long.class, new Func1<String, Object>() {
      @Override
      public Object call(String value) {
        return Long.parseLong(value);
      }
    });

    addConverter("float", float.class, new Func1<String, Object>() {
      @Override
      public Object call(String value) {
        return Float.parseFloat(value);
      }
    });

    addConverter("double", double.class, new Func1<String, Object>() {
      @Override
      public Object call(String value) {
        return Double.parseDouble(value);
      }
    });

    // default date
    addConverter("date", Date.class, new Func1<String, Object>() {
      @Override
      public Object call(String value) {
        // attempt to parse long
        try {
          return new Date(Long.parseLong(value));
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

  public void addConverter(String name, Class<?> type, Func1<String, ? extends Object> converter) {
    this.namesToType.put(name, type);
    this.typeToConverter.put(type, converter);
  }

  public Object convert(String value, Class<?> type) {
    return typeToConverter
      .get(type)
      .call(value);
  }

  public Object convert(String value, String typeName) {
    if (typeName == null || typeName.isEmpty()) {
      typeName = "String";
    }

    return convert(value, this.namesToType.get(typeName));
  }

  public Class<?> toType(String name) throws Exception {
    if (!this.namesToType.containsKey(name)) {
      throw new Exception("Type of name " + name + " not found");
    }

    return this.namesToType.get(name);
  }

  public Func1<String, ? extends Object> getConverter(Class<?> type) {
    return this.typeToConverter.get(type);
  }
}
