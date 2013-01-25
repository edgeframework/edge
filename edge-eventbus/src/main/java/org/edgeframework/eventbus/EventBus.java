package org.edgeframework.eventbus;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.swing.text.StyledEditorKit.BoldAction;

import org.edgeframework.promises.Promise;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.deploy.impl.VertxLocator;

public final class EventBus {

  private static final String MESSAGE_RESULT_KEY = "result";

  @SuppressWarnings("unchecked")
  public static <T> T createProxy(final Class<T> clazz) {

    // for (Method method : clazz.getMethods()) {
    // // Ignore Object methods
    // if (method.getDeclaringClass().equals(Object.class)) {
    // continue;
    // }
    //
    // System.out.println(method.getName());
    // System.out.println(method.getAnnotations().length);
    // }

    T t = (T) Proxy.newProxyInstance(
        clazz.getClassLoader(),
        new Class[] { clazz },
        new InvocationHandler() {
          @Override
          public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String methodName = method.getName();

            Promise<String> p = null;
            Handler<Message<JsonObject>> replyHandler = null;

            if (method.getReturnType().equals(void.class) || method.getReturnType().equals(Void.class)) {
              System.out.println("No Return Type");
            } else {
              System.out.println("Has Return Type, assuming its a Promise");

              p = Promise.defer();
              final Promise<String> promise = p;
              replyHandler = new Handler<Message<JsonObject>>() {
                @Override
                public void handle(Message<JsonObject> reply) {
                  promise.fulfill(reply.body.getString(MESSAGE_RESULT_KEY));
                }
              };
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

            System.out.println(jsonMessage.toString());

            VertxLocator.vertx.eventBus().send(methodName, jsonMessage, replyHandler);
            return p;
          }
        });

    return t;
  }

  public static <T> void registerReceiver(final T receiver) {
    Class<?> clazz = receiver.getClass();
    if (clazz.isAnonymousClass()) {
      clazz = clazz.getInterfaces()[0];
    }

    org.vertx.java.core.eventbus.EventBus eb = VertxLocator.vertx.eventBus();

    MethodHandles.Lookup lookup = MethodHandles.lookup().in(clazz);

    for (Method m : clazz.getMethods()) {
      // Ignore Object methods
      if (m.getDeclaringClass().equals(Object.class)) {
        continue;
      }

      try {
        final MethodHandle handle = lookup.unreflect(m);
        eb.registerHandler(m.getName(), new Handler<Message<JsonObject>>() {
          @Override
          public void handle(Message<JsonObject> message) {
            try {
              Object result = handle.invokeWithArguments(receiver, message.body.getString("message"));
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

    if (value instanceof byte[]) {
      json.putBinary(fieldName, (byte[]) value);
    }

    // TODO: Support Objects and Arrays?
  }
}
