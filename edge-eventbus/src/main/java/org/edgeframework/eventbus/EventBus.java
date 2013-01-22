package org.edgeframework.eventbus;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.deploy.impl.VertxLocator;

import com.darylteo.edge.test.util.TestEventBusSender;

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

            if (args == null || args.length == 0) {
              VertxLocator.vertx.eventBus().send(methodName, (JsonObject) null);
            } else {
              // TODO: Serialize
              VertxLocator.vertx.eventBus().send(methodName, args[0].toString());
            }

            return null;
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
      System.out.println(m.getName());
      if (m.getDeclaringClass().equals(Object.class)) {
        continue;
      }

      try {
        final MethodHandle handle = lookup.unreflect(m);
        eb.registerHandler(m.getName(), new Handler<Message<?>>() {
          @Override
          public void handle(Message<?> message) {
            try {
              handle.invoke(receiver, message.body);
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
