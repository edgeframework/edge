package org.edgeframework.promises.test;

import org.edgeframework.promises.FailureHandler;
import org.edgeframework.promises.FailureHandler;
import org.edgeframework.promises.Promise;
import org.edgeframework.promises.PromiseHandler;
import org.edgeframework.promises.RepromiseHandler;
import org.vertx.groovy.core.Vertx;
import org.vertx.java.core.Handler;
import org.vertx.java.core.SimpleHandler;
import org.vertx.java.deploy.impl.VertxLocator;
import org.vertx.java.testframework.TestClientBase;

import rx.Observable;
import rx.util.functions.Action1;

public class PromiseTestClient extends TestClientBase {

  @Override
  public void start() {
    super.start();
    tu.appReady();
  }

  @Override
  public void stop() {
    super.stop();
  }

  public Promise<String> makePromise(final String message) {
    final Promise<String> promise = Promise.defer();

    vertx.runOnLoop(new SimpleHandler() {
      @Override
      public void handle() {
        System.out.print("Working.");
        for (int i = 0; i < 10; i++) {
          System.out.print(".");

          try {
            Thread.sleep(100);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }

        System.out.println();
        promise.fulfill(message);
      }
    });

    return promise;
  }

  public void testDefer() throws Exception {
    Promise<String> promise = Promise.defer();

    tu.azzert(promise != null);
    tu.azzert(promise instanceof Promise);
    tu.testComplete();
  }

  public void testDefer2() throws Exception {
    Promise<String> promise = makePromise("Hello World");

    tu.azzert(promise != null);
    tu.azzert(promise instanceof Promise);
    tu.testComplete();
  }

  /* Basic handler */
  public void testBasic() throws Exception {
    makePromise("Hello World")
        .then(new PromiseHandler<String, Void>() {
          @Override
          public Void handle(String result) {
            tu.azzert(result.equals("Hello World"), "Promise failed to pass through return value to handler");
            tu.testComplete();
            return null;
          }
        });
  }

  /* Test of Handlers - return Value */
  public void testChain1() throws Exception {
    makePromise("Hello World")
        .then(new PromiseHandler<String, String>() {
          @Override
          public String handle(String result) {
            return result.toUpperCase();
          }
        })
        .then(new PromiseHandler<String, Void>() {
          @Override
          public Void handle(String result) {
            tu.azzert(result.equals("HELLO WORLD"), "Promise failed to pass through return value to handler in a chain");
            tu.testComplete();
            return null;
          }
        });
  }

  /* Chain of handlers - return Promise */
  public void testChain2() throws Exception {
    makePromise("Hello World")
        .then(new RepromiseHandler<String, String>() {
          @Override
          public Promise<String> handle(final String result) {
            return makePromise("Foo Bar");
          }
        })
        .then(new PromiseHandler<String, Void>() {
          @Override
          public Void handle(String result) {
            tu.azzert(result.equals("Foo Bar"), "Promise failed to pass re-promise through in chain.");
            tu.testComplete();
            return null;
          }
        });
  }

  /* Exception with then() handler */
  public void testException1() throws Exception {
    makePromise("Hello World")
        .then(new PromiseHandler<String, Character>() {
          @Override
          public Character handle(String result) {
            return result.charAt(20); // Exception
          }
        }).then(
            new PromiseHandler<Character, Void>() {
              @Override
              public Void handle(Character value) {
                tu.azzert(false, "Promise not correctly calling failure handler when exception or rejection occurs");
                tu.testComplete();
                return null;
              }
            },
            new FailureHandler<Void>() {
              @Override
              public Void handle(Exception value) {
                tu.testComplete();
                return null;
              }
            }
        );
  }

  /* Exception with fail() handler */
  public void testException2() throws Exception {
    makePromise("Hello World")
        .then(new PromiseHandler<String, Character>() {
          @Override
          public Character handle(String result) {
            return result.charAt(20); // Exception
          }
        }).fail(
            new FailureHandler<Void>() {
              @Override
              public Void handle(Exception value) {
                tu.testComplete();
                return null;
              }
            }
        );
  }

  /* Exception with fail() handler */
  public void testException3() throws Exception {
    makePromise("Hello World")
        .then(
            new PromiseHandler<String, Character>() {
              @Override
              public Character handle(String result) {
                return result.charAt(20); // Exception
              }
            },
            new FailureHandler<Character>() {
              @Override
              public Character handle(Exception e) {
                tu.azzert(false, "This rejection handler should not be called!");
                return null;
              }
            }
        ).fail(
            new FailureHandler<Void>() {
              @Override
              public Void handle(Exception value) {
                tu.testComplete();
                return null;
              }
            }
        );
  }

  /* Exception with handler */
  public void testException4() throws Exception {
    makePromise("Hello World")
        .then(new PromiseHandler<String, Character>() {
          @Override
          public Character handle(String result) {
            return result.charAt(20); // Exception
          }
        }).then(
            new PromiseHandler<Character, String>() {
              @Override
              public String handle(Character value) {
                tu.azzert(false, "Promise not correctly calling failure handler when exception or rejection occurs");
                tu.testComplete();
                return "The Char is : " + value;
              }
            },
            new FailureHandler<String>() {
              @Override
              public String handle(Exception value) {
                return null;
              }
            }
        ).then(new PromiseHandler<String, Void>() {
          @Override
          public Void handle(String value) {
            tu.azzert(value == null);
            tu.testComplete();
            return null;
          }
        });
  }

  /*
   * Exception with handler - there is no way for JUnit to pick up the
   * exception: verify visually through stacktrace
   */
  public void testException5() throws Exception {
    makePromise("Hello World")
        .then(new PromiseHandler<String, Character>() {
          @Override
          public Character handle(String result) {
            return result.charAt(20); // Exception
          }
        });
  }

  public void testException6() throws Exception {
    makePromise("Hello World")
        .then(new PromiseHandler<String, Character>() {
          @Override
          public Character handle(String result) {
            return result.charAt(20); // Exception
          }
        })
        .then(new PromiseHandler<Character, Void>() {
          @Override
          public Void handle(Character value) {
            tu.azzert(false, "Promise should not execute this due to exception");
            return null;
          }
        });
  }

  /* Fin with basic */
  public void testFinally1() throws Exception {
    makePromise("Hello World")
        .then(new PromiseHandler<String, Void>() {
          @Override
          public Void handle(String result) {
            return null;
          }
        })
        .fin(new PromiseHandler<Void, String>() {

          @Override
          public String handle(Void value) {
            tu.testComplete();
            return "Finally!";
          }
        });
  }

  /* Fin with Exception */
  public void testFinally2() throws Exception {
    makePromise("Hello World")
        .then(new PromiseHandler<String, Character>() {
          @Override
          public Character handle(String result) {
            char c = ' ';
            c = result.charAt(20); // Exception

            return c;
          }
        })
        .fin(new PromiseHandler<Void, String>() {
          @Override
          public String handle(Void value) {
            tu.testComplete();
            return "Finally!";
          }
        });
  }

  public void testPrefilled() throws Exception {
    Promise<String> p = Promise.defer();

    p.fulfill("Hello World");

    p.then(new PromiseHandler<String, Void>() {
      @Override
      public Void handle(String value) {
        tu.azzert(value.equals("Hello World"));
        tu.testComplete();
        return null;
      }
    });
  }

  public void testRxBasic() {
    makePromise("Hello World")
        .subscribe(new Action1<String>() {
          @Override
          public void call(String value) {
            tu.azzert(value.equals("Hello World"));
            tu.testComplete();
          }
        });

  }
}
