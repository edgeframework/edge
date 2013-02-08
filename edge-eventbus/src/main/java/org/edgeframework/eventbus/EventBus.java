package org.edgeframework.eventbus;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

import org.edgeframework.promises.Promise;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.deploy.impl.VertxLocator;

public final class EventBus {

  private static final String MESSAGE_RESULT_KEY = "result";
  private static final MethodHandles.Lookup lookup = MethodHandles.lookup();

  private static final org.vertx.java.core.eventbus.EventBus eb = VertxLocator.vertx.eventBus();

  @SuppressWarnings("unchecked")
  public static <T> T registerSender(final String namespace, final Class<T> clazz) {
    T t = (T) Proxy.newProxyInstance(
        clazz.getClassLoader(),
        new Class[] { clazz },
        new InvocationHandler() {
          @Override
          public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String methodName = method.getName();

            final Promise<?> promise;
            Handler<Message<JsonObject>> replyHandler = null;
            Type returnType = method.getGenericReturnType();

            if (requirePromise(returnType)) {
              promise = createReturnPromise(returnType.getClass());

              final MethodHandle fulfillHandle = lookup.findVirtual(promise.getClass(), "fulfill", MethodType.methodType(void.class, Object.class));

              replyHandler = new Handler<Message<JsonObject>>() {
                @Override
                public void handle(Message<JsonObject> reply) {
                  try {
                    fulfillHandle.invokeWithArguments(promise, reply.body.getString(MESSAGE_RESULT_KEY));
                  } catch (Throwable e) {
                    e.printStackTrace();
                    VertxLocator.container.getLogger().error(e);
                  }
                }
              };
            } else {
              promise = null;
            }

            String[] paramNames = ((EventBusParams) method.getAnnotation(EventBusParams.class)).value();
            // TODO: eventbus params not present (null)
            if (paramNames.length != args.length) {
              // TODO: invalid eventbus params length
            }

            JsonObject jsonMessage = null;

            if (!(args == null || args.length == 0)) {
              jsonMessage = new JsonObject();
              for (int i = 0; i < paramNames.length; i++) {
                String name = paramNames[i];
                Object arg = args[i];

                addParam(name, arg, jsonMessage);
              }
            }

            VertxLocator.container.getLogger().debug(jsonMessage.toString());

            String address = namespace.isEmpty() ? methodName : String.format("%s.%s", namespace, methodName);

            eb.send(address, jsonMessage, replyHandler);
            return promise;
          }
        });

    return t;
  }

  public static <T> void registerReceiver(String namespace, final T receiver, Class<T> clazz) {
    for (Method method : clazz.getMethods()) {
      // Ignore Object methods
      if (method.getDeclaringClass().equals(Object.class)) {
        continue;
      }

      final String[] paramNames = ((EventBusParams) method.getAnnotation(EventBusParams.class)).value();
      final Class<?>[] paramTypes = method.getParameterTypes();
      // TODO: eventbus params not present (null)

      try {
        final MethodHandle handle = lookup.unreflect(method);
        String address = namespace.isEmpty() ? method.getName() : String.format("%s.%s", namespace, method.getName());

        eb.registerHandler(address, new Handler<Message<JsonObject>>() {
          @Override
          public void handle(Message<JsonObject> message) {
            List<Object> params = messageToParams(receiver, paramNames, paramTypes, message.body);

            try {
              Object result = handle.invokeWithArguments(params);
              if (!handle.type().returnType().equals(void.class) && !handle.type().returnType().equals(Void.class)) {
                // Return Type is valid, so we need to send it back
                JsonObject jsonMessage = new JsonObject();
                addParam(MESSAGE_RESULT_KEY, result, jsonMessage);

                message.reply(jsonMessage);
              }
            } catch (Throwable e) {
              e.printStackTrace();
              VertxLocator.container.getLogger().error(e);
            }
          }
        });

      } catch (IllegalAccessException e) {
        e.printStackTrace();
        VertxLocator.container.getLogger().error(e);
      }
    }
  }

  private static void addParam(String fieldName, Object value, JsonObject json) throws Exception {
    if (value instanceof String) {
      json.putString(fieldName, (String) value);
      return;
    }

    if (value instanceof Number) {
      json.putNumber(fieldName, (Number) value);
      return;
    }

    if (value instanceof Boolean) {
      json.putBoolean(fieldName, (Boolean) value);
    }

    // TODO: Support for Objects/Arrays and Complex Types
  }

  private static List<Object> messageToParams(Object target, String[] names, Class<?>[] types, JsonObject message) {
    List<Object> params = new LinkedList<>();
    params.add(target);

    for (int i = 0; i < names.length; i++) {
      String name = names[i];
      Class<?> type = types[i];

      if (type.equals(String.class)) {
        params.add(message.getString(name));
      } else if (type.equals(Boolean.class) || type.equals(boolean.class)) {
        params.add(message.getBoolean(name));
      } else if (type.equals(Byte.class) || type.equals(byte.class)) {
        params.add(message.getNumber(name).byteValue());
      } else if (type.equals(Short.class) || type.equals(short.class)) {
        params.add(message.getNumber(name).shortValue());
      } else if (type.equals(Integer.class) || type.equals(int.class)) {
        params.add(message.getNumber(name).intValue());
      } else if (type.equals(Long.class) || type.equals(long.class)) {
        params.add(message.getNumber(name).longValue());

      } else if (type.equals(Float.class) || type.equals(float.class)) {
        params.add(message.getNumber(name).floatValue());
      } else if (type.equals(Double.class) || type.equals(double.class)) {
        params.add(message.getNumber(name).doubleValue());
      } else if (Number.class.isAssignableFrom(type)) {
        params.add(message.getNumber(name));
      } else {
        System.out.println(type);
        System.out.println("FAIL!");
      }

      // TODO: Support for Objects/Arrays and Complex Types
    }

    return params;
  }

  private static boolean requirePromise(Type returnType) {
    if (returnType.equals(void.class) || returnType.equals(Void.class)) {
      return false;
    }

    return true;
  }

  // Workaround to instantiate new Promise by using type wildcard
  private static <T> Promise<T> createReturnPromise(Class<T> returnType) throws Exception {
    return Promise.defer(); // 30 second timeout
  }
}
