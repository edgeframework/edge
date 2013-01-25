package org.edgeframework.promises;

import org.vertx.java.core.Handler;
import org.vertx.java.deploy.impl.VertxLocator;

/**
 * A Promise represents a request that will be fulfilled sometime in the future,
 * most usually by an asynchrous task executed on the Vert.x Event Loop. It
 * allows you to assign handlers to deal with the return results of asynchronus
 * tasks, and to flatten "pyramids of doom" or "callback hell".
 * 
 * @author Daryl Teo
 * 
 * @param T
 *          - the data type of the result contained by this Promise.
 */
public class Promise<T> {

  private Promise<T> that = this;

  public static enum State {
    PENDING,
    FULFILLED,
    REJECTED
  }

  // Handler that is called when the promise is fulfilled
  private CompletedHandler<T> fulfilled;
  private FailureHandler<?> rejected;
  private CompletedHandler<Void> finaled;

  // A deferred promise that is chained on top of this one
  @SuppressWarnings("rawtypes")
  private Promise deferred;

  // The result that can be retrieved from the Promise
  private T result;

  private Promise.State state;

  private long timerTimeout = -1;
  private long timerId = -1;

  private Promise() {
    this.state = Promise.State.PENDING;
  }

  /**
   * Chains a promise handler and returns a new Promise that is fulfilled by
   * that handler. This handler will be executed on the next event loop.
   * 
   * @param fulfilled
   *          - the handler that is called when the Promise is fulfilled
   * @return the new deferred Promise.
   */
  public <O> Promise<O> then(PromiseHandler<T, O> fulfilled) {
    return this.then(fulfilled, null, -1);
  }

  /**
   * Chains a promise handler and returns a new Promise that is fulfilled by
   * that handler. This handler will be executed on the next event loop.
   * 
   * @param fulfilled
   *          - the handler that is called when the Promise is fulfilled
   * @param rejected
   *          - the handler that is called when the Promise is rejected
   * @return the new deferred Promise.
   */
  public <O> Promise<O> then(PromiseHandler<T, O> fulfilled, FailureHandler<O> rejected) {
    return this.then(fulfilled, rejected, -1);
  }

  /**
   * Chains a promise handler and returns a new Promise that is fulfilled by
   * that handler. This handler will be executed on the next event loop.
   * 
   * @param fulfilled
   *          - the handler that is called when the Promise is fulfilled
   * @param timeout
   *          - number of milliseconds before failure handler is triggered
   * @return the new deferred Promise.
   */
  public <O> Promise<O> then(PromiseHandler<T, O> fulfilled, long timeout) {
    return this.then(fulfilled, null, timeout);
  }

  /**
   * Chains a promise handler and returns a new Promise that is fulfilled by
   * that handler. This handler will be executed on the next event loop.
   * 
   * @param fulfilled
   *          - the handler that is called when the Promise is fulfilled
   * @param rejected
   *          - the handler that is called when the Promise is rejected
   * @param timeout
   *          - number of milliseconds before failure handler is triggered
   * @return the new deferred Promise.
   */
  public <O> Promise<O> then(PromiseHandler<T, O> fulfilled, FailureHandler<O> rejected, long timeout) {
    this.fulfilled = fulfilled;
    this.rejected = rejected;

    Promise<O> deferred = Promise.defer();
    this.deferred = deferred;
    this.deferred.timerTimeout = timeout;

    return deferred;
  }

  /**
   * Chains a promise handler and returns a new Promise that is fulfilled by
   * that handler. This handler will be executed on the next event loop. The
   * handler must return a new promise that can then be subsequently handled.
   * This handler must fulfill the returned promise.
   * 
   * @param fulfilled
   *          - the handler that is called when the Promise is fulfilled
   * @return the new deferred Promise.
   */
  public <O> Promise<O> then(RepromiseHandler<T, O> fulfilled) {
    return this.then(fulfilled, null, -1);
  }

  /**
   * Chains a promise handler and returns a new Promise that is fulfilled by
   * that handler. This handler will be executed on the next event loop. The
   * handler must return a new promise that can then be subsequently handled.
   * This handler must fulfill the returned promise.
   * 
   * @param fulfilled
   *          - the handler that is called when the Promise is fulfilled
   * @param timeout
   *          - number of milliseconds before failure handler is triggered
   * @return the new deferred Promise.
   */
  public <O> Promise<O> then(RepromiseHandler<T, O> fulfilled, long timeout) {
    return this.then(fulfilled, null, timeout);
  }

  /**
   * Chains a promise handler and returns a new Promise that is fulfilled by
   * that handler. This handler will be executed on the next event loop. The
   * handler must return a new promise that can then be subsequently handled.
   * This handler must fulfill the returned promise.
   * 
   * @param fulfilled
   *          - the handler that is called when the Promise is fulfilled
   * @param rejected
   *          - the handler that is called when the Promise is rejected
   * @return the new deferred Promise.
   */
  public <O> Promise<O> then(RepromiseHandler<T, O> fulfilled, FailureHandler<O> rejected) {
    return this.then(fulfilled, rejected, -1);
  }

  /**
   * Chains a promise handler and returns a new Promise that is fulfilled by
   * that handler. This handler will be executed on the next event loop. The
   * handler must return a new promise that can then be subsequently handled.
   * This handler must fulfill the returned promise.
   * 
   * @param fulfilled
   *          - the handler that is called when the Promise is fulfilled
   * @param rejected
   *          - the handler that is called when the Promise is rejected
   * @param timeout
   *          - number of milliseconds before failure handler is triggered
   * @return the new deferred Promise.
   */
  public <O> Promise<O> then(RepromiseHandler<T, O> fulfilled, FailureHandler<O> rejected, long timeout) {
    this.fulfilled = fulfilled;
    this.rejected = rejected;

    Promise<O> deferred = Promise.defer();
    this.deferred = deferred;
    this.deferred.timerTimeout = timeout;

    return deferred;
  }

  /**
   * This chains a handler and returns a new Promise. However, calling this
   * method on a chain of promises indicates that there are no further work to
   * be done. This is analogous to a "finally" clause in Java. You should only
   * use this to perform cleanup operations.
   * 
   * The handler will be invoked regardless of the success/failure of the
   * previous handler, but will only be invoked upon success or failure
   * (otherwise, it will simply time out)
   * 
   * @param handler
   *          - the handler that is called when the promise is resolved.
   * @return the new deferred Promise.
   */
  public <O> Promise<O> fin(PromiseHandler<Void, O> handler) {
    this.finaled = handler;

    Promise<O> deferred = Promise.defer();
    this.deferred = deferred;

    return deferred;
  }

  /**
   * This chains a handler and returns a new Promise. This is shorthand for
   * "then()", but with null as the fulfilment handler. Useful if you're only
   * expecting an exception and not worried about the return value.
   * 
   * @param rejected
   *          - the handler that is called when the promise is rejected.
   * @return the new deferred Promise.
   * @see #then(PromiseHandler, FailureHandler)
   */
  public <O> Promise<O> fail(FailureHandler<O> rejected) {
    this.rejected = rejected;

    Promise<O> deferred = Promise.defer();
    this.deferred = deferred;

    return deferred;
  }

  /**
   * For internal use only. Turns this Promise into the passed in Promise. All
   * handlers on the passed in Promise are nulled.
   * 
   * @param other
   *          - the Promise to become.
   */
  void become(Promise<T> other) {
    this.deferred = other.deferred;
    other.deferred = null;

    this.fulfilled = other.fulfilled;
    other.fulfilled = null;

    this.finaled = other.finaled;
    other.finaled = null;
  }

  /**
   * Returns the result of this promise
   * 
   * @return
   * @throws IllegalStateException
   *           - thrown if the promise is not in a fulfilled state
   */
  public T get() {
    if (this.state != State.FULFILLED) {
      throw new IllegalStateException("Promise has not been fulfilled yet");
    }
    return this.result;
  }

  /**
   * Fulfills the promise with the value given.
   * 
   * @param result
   *          - the value to fulfill the promise with.
   */
  public void fulfill(final T result) {
    if (this.state != State.PENDING) {
      return;
    }

    this.result = result;
    this.state = State.FULFILLED;

    // Begin any timeout handlers for the deferred promise
    if (this.deferred != null) {
      this.deferred.beginTimeout();
    }

    Promise.enqueue(new Runnable() {
      @SuppressWarnings("unchecked")
      @Override
      public void run() {
        try {
          Object resolved = null;

          if (that.fulfilled != null) {
            resolved = that.fulfilled.handle(result);
          }

          if (that.finaled != null) {
            Promise.enqueue(new Runnable() {
              @Override
              public void run() {
                that.finaled.handle(null);
              }
            });
          } else if (that.deferred != null) {
            /*
             * Hopefully, an occurance of a ClassCastException should never
             * occur, due to the type safety on the client side code
             */
            if (resolved instanceof Promise) {
              final Promise<T> p = ((Promise<T>) resolved);
              p.become(that.deferred);
            } else {
              that.deferred.fulfill(resolved);
            }
          }
        } catch (Throwable e) {
          /*
           * Catch and pass any exceptions that occur in the handler to the
           * failure handler for the promise (if any)
           */
          if (that.finaled != null) {
            Promise.enqueue(new Runnable() {
              @Override
              public void run() {
                that.finaled.handle(null);
              }
            });
          } else if (that.deferred != null) {
            that.deferred.reject(e);
          }
        }
      }
    });

  }

  /**
   * Rejects the promise, specifying the Throwable that caused the rejection.
   * 
   * @param e
   *          - the exception that caused the rejection.
   */
  public void reject(final Throwable e) {
    if (this.state != State.PENDING) {
      return;
    }

    this.result = null;
    this.state = State.REJECTED;

    // Begin any timeout handlers for the deferred promise
    if (this.deferred != null) {
      this.deferred.beginTimeout();
    }

    Promise.enqueue(new Runnable() {
      @SuppressWarnings("unchecked")
      @Override
      public void run() {

        if (that.finaled != null) {
          Promise.enqueue(new Runnable() {
            @Override
            public void run() {
              that.finaled.handle(null);
            }
          });
        } else if (that.rejected != null) {
          Object resolved = that.rejected.handle(e);

          if (that.deferred != null) {
            if (resolved instanceof Promise) {
              final Promise<T> p = ((Promise<T>) resolved);
              p.become(that.deferred);
            } else {
              that.deferred.fulfill(resolved);
            }
          }
        } else {
          VertxLocator.container.getLogger().error("No error handler defined for this promise");
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * Rejects the promise, specifying the reason that caused the rejection.
   * 
   * @param reason
   *          - the reason for the rejection. This is then wrapped as an
   *          Exception.
   */
  public void reject(String reason) {
    this.reject(new Exception(reason));
  }

  private void beginTimeout() {
    this.timerId = this.setTimeout(this.timerTimeout);
  }

  private long setTimeout(long timeout) {
    if (timeout < 0) {
      return -1;
    }

    return VertxLocator.vertx.setTimer(timeout, new Handler<Long>() {
      @Override
      public void handle(Long timerid) {
        Promise<?> that = Promise.this;
        System.out.println(that.state);
        if (that.state == State.PENDING) {
          that.reject("Promise Timed Out");
        }
      }
    });
  }

  public static <T> Promise<T> defer() {
    return Promise.defer(null, -1);
  }

  public static <T> Promise<T> defer(final Handler<Promise<T>> promiser) {
    return Promise.defer(promiser, -1);
  }

  public static <T> Promise<T> defer(final Handler<Promise<T>> promiser, long timeout) {
    final Promise<T> promise = new Promise<>();

    if (promiser != null) {
      Promise.enqueue(new Runnable() {
        @Override
        public void run() {
          promiser.handle(promise);
        }
      });
    }

    promise.timerTimeout = timeout;
    promise.beginTimeout();

    return promise;
  }

  private static void enqueue(final Runnable task) {
    VertxLocator.vertx.runOnLoop(new Handler<Void>() {
      @Override
      public void handle(Void event) {
        task.run();
      }
    });
  }
}
