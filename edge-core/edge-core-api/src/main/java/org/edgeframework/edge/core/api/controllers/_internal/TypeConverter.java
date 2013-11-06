package org.edgeframework.edge.core.api.controllers._internal;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TypeConverter {
  private Map<String, Class<?>> namesToType = new HashMap<>();
  private Map<Class<?>, TypeConverterFunction<String, ? extends Object>> typeToConverter = new HashMap<>();

  public TypeConverter() {
    // default string types
    addConverter("string", String.class, new TypeConverterFunction<String, Object>() {
      @Override
      public Object call(String value) {
        return value;
      }
    });

    // default primitives
    addConverter("byte", byte.class, new TypeConverterFunction<String, Object>() {
      @Override
      public Object call(String value) {
        return Byte.parseByte(value);
      }
    });

    addConverter("short", short.class, new TypeConverterFunction<String, Object>() {
      @Override
      public Object call(String value) {
        return Short.parseShort(value);
      }
    });

    addConverter("int", int.class, new TypeConverterFunction<String, Object>() {
      @Override
      public Object call(String value) {
        return Integer.parseInt(value);
      }
    });

    addConverter("long", long.class, new TypeConverterFunction<String, Object>() {
      @Override
      public Object call(String value) {
        return Long.parseLong(value);
      }
    });

    addConverter("float", float.class, new TypeConverterFunction<String, Object>() {
      @Override
      public Object call(String value) {
        return Float.parseFloat(value);
      }
    });

    addConverter("double", double.class, new TypeConverterFunction<String, Object>() {
      @Override
      public Object call(String value) {
        return Double.parseDouble(value);
      }
    });

    // default date
    addConverter("date", Date.class, new TypeConverterFunction<String, Object>() {
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

  public void addConverter(String name, Class<?> type, TypeConverterFunction<String, ? extends Object> converter) {
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

  public TypeConverterFunction<String, ? extends Object> getConverter(Class<?> type) {
    return this.typeToConverter.get(type);
  }
}
