package org.edgeframework.eventbus;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.edgeframework.promises.Promise;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.deploy.impl.VertxLocator;

public final class EventBus {

  @SuppressWarnings("unchecked")
  public static <T> T createProxy(final Class<T> clazz) {

    // for (Method method : clazz.getMethods()) {
    // // Ignore Object methods
    // if (method.getDeclaringClass().equals(Object.class)) {
    // continue;
    // }
    //
    // System.out.println(method.getName());
    // }

    T t = (T) Proxy.newProxyInstance(
        clazz.getClassLoader(),
        new Class[] { clazz },
        new InvocationHandler() {
          @Override
          public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String methodName = method.getName();

            Promise<String> p = null;
            Handler<Message<String>> replyHandler = null;

            if (method.getReturnType().equals(void.class) || method.getReturnType().equals(Void.class)) {
              System.out.println("No Return Type");
            } else {
              System.out.println("Has Return Type, assuming its a Promise");

              p = Promise.defer();
              final Promise<String> promise = p;
              replyHandler = new Handler<Message<String>>() {
                @Override
                public void handle(Message<String> reply) {
                  promise.fulfill(reply.body);
                }
              };
            }

            if (args == null || args.length == 0) {
              VertxLocator.vertx.eventBus().send(methodName, (String) null, replyHandler);
            } else {
              // TODO: Serialize
              VertxLocator.vertx.eventBus().send(methodName, args[0].toString(), replyHandler);
            }

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
        eb.registerHandler(m.getName(), new Handler<Message<String>>() {
          @Override
          public void handle(Message<String> message) {
            try {
              Object result = handle.invokeWithArguments(receiver, message.body);
              if (!handle.type().returnType().equals(void.class) && !handle.type().returnType().equals(Void.class)) {
                // Return Type is valid, so we need to send it back
                message.reply((String) result);
              }
            } catch (Throwable e) {
              VertxLocator.container.getLogger().error(e);
            }
          }
        });

      } catch (IllegalAccessException e) {
        VertxLocator.container.getLogger().error(e);
      }
    }
  }
}
