package com.darylteo.edge.core.requests;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class QueryParser {

  private static class Parameter {
    private static final Pattern PARAM_KEY_PATTERN = Pattern.compile("^(?<key>[A-Za-z0-9]+)(?<array>\\[(?<property>.*)\\])?(=(?<value>.*))?$");

    public String key;
    public String value;
    public String property;

    public boolean isArray;
    public boolean isMap;
    public boolean isValid;

    public Parameter(String parameter) {
      Matcher matcher = PARAM_KEY_PATTERN.matcher(parameter);

      isValid = matcher.matches();
      if (!isValid) {
        return;
      }

      this.key = matcher.group("key");
      this.value = matcher.group("value");
      String arraySection = matcher.group("array");

      if (arraySection != null) {
        this.property = matcher.group("property");
        this.isMap = this.property.length() > 0;
        this.isArray = !this.isMap;
      }
    }
  }

  public static Map<String, Object> parse(String queryString) {
    Map<String, Object> query = new HashMap<>();

    if (queryString == null) {
      return query;
    }

    /* Split up the parameters */
    List<Parameter> parameters = new LinkedList<>();

    for (String parameter : queryString.split("&")) {
      /* Separate each key value pair */
      Parameter p = new Parameter(parameter);
      if (p.isValid) {
        parameters.add(p);
      }
    }

    Collections.sort(parameters, new Comparator<Parameter>() {
      @Override
      public int compare(Parameter p1, Parameter p2) {
        if (p1.key.equals(p2.key)) {
          if (p1.isArray == p2.isArray) {
            return 0;
          } else {
            return p1.isArray ? 1 : -1;
          }
        } else {
          return p1.key.compareTo(p2.key);
        }
      }
    });

    for (Parameter parameter : parameters) {
      System.out.println("Key: " + parameter.key);
      System.out.println("Value: " + parameter.value);
      System.out.println("IsArray: " + parameter.isArray);
      System.out.println("IsMap: " + parameter.isMap);

      boolean entered = query.containsKey(parameter.key);

      if (parameter.isArray) {
        List<Object> list;

        if (entered) {
          Object existing = query.get(parameter.key);

          if (existing instanceof List<?>) {
            list = (List<Object>) existing;
          } else {
            list = new LinkedList<>();
            query.put(parameter.key, list);
          }
        } else {
          list = new LinkedList<>();
          query.put(parameter.key, list);
        }

        list.add(parameter.value);

      } else if (parameter.isMap) {

        Map<String, Object> map;
        if (entered) {
          Object existing = query.get(parameter.key);
          if (existing instanceof Map<?, ?>) {
            map = (Map<String, Object>) existing;
          } else {
            map = new HashMap<>();
            query.put(parameter.key, map);
          }
        } else {
          map = new HashMap<>();
          query.put(parameter.key, map);
        }

        map.put(parameter.property, parameter.value);

      } else {
        query.put(parameter.key, parameter.value);
      }
    }

    return query;
  }

}
