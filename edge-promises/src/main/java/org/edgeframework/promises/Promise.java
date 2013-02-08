package org.edgeframework.promises;

import java.util.Map;

import org.jboss.netty.util.internal.ConcurrentHashMap;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.util.AtomicObservableSubscription;
import rx.util.functions.Func1;

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
public class Promise<T> extends Observable<T> {
  private Promise<T> that = this;
  private T value = null;

  // private CompletedHandler<Void> onFinally = null;
  // private CompletedHandler<T> onFulfilled = null;
  // private CompletedHandler<Throwable> onRejected = null;

  private Map<Subscription, Observer<T>> observers;

  public static <T> Promise<T> defer() {
    final ConcurrentHashMap<Subscription, Observer<T>> observers = new ConcurrentHashMap<Subscription, Observer<T>>();
    final Promise<T> promise = new Promise<>(
        new Func1<Observer<T>, Subscription>() {
          @Override
          public Subscription call(Observer<T> observer) {
            final AtomicObservableSubscription subscription = new AtomicObservableSubscription();

            subscription.wrap(new Subscription() {
              @Override
              public void unsubscribe() {
                // on unsubscribe remove it from the map of outbound observers
                // to
                // notify
                observers.remove(subscription);
              }
            });

            observers.put(subscription, observer);
            return subscription;
          }
        },
        observers
        );

    return promise;
  }

  protected Promise(Func1<Observer<T>, Subscription> onSubscribe, Map<Subscription, Observer<T>> observers) {
    super(onSubscribe);
    this.observers = observers;
  }

  /* Defer Methods */
  public <O> Promise<O> then(PromiseHandler<T, O> onFulfilled) {
    return this._then(onFulfilled, null, null);
  }

  public <O> Promise<O> then(PromiseHandler<T, O> onFulfilled, FailureHandler<?> onRejected) {
    return this._then(onFulfilled, onRejected, null);
  }

  public <O> Promise<O> then(RepromiseHandler<T, O> onFulfilled) {
    return this._then(onFulfilled, null, null);
  }

  public <O> Promise<O> then(RepromiseHandler<T, O> onFulfilled, FailureHandler<O> onRejected) {
    return this._then(onFulfilled, onRejected, null);
  }

  public <O> Promise<O> fail(FailureHandler<O> onRejected) {
    return this._then(null, onRejected, null);
  }

  public <O> Promise<O> fin(PromiseHandler<Void, O> onFinally) {
    return this._then(null, null, onFinally);
  }

  private <O> Promise<O> _then(
      final CompletedHandler<T> onFulfilled,
      final CompletedHandler<Exception> onRejected,
      final CompletedHandler<Void> onFinally)
  {
    final Promise<O> promise = Promise.defer();
    this.subscribe(new Observer<T>() {
      @Override
      public void onCompleted() {
        // No op
      }

      @Override
      @SuppressWarnings("unchecked")
      public void onError(Exception reason) {
        Object result = null;

        try {
          // Perform any finally handlers
          if (onFinally != null) {
            result = onFinally.handle(null);

            // results from fin() handler is ignored
            if (!(result instanceof Promise)) {
              result = null;
            }
          } else if (onRejected == null) {
            // We don't have a handler so we'll just forward on
            // We have to assume that the casting will work...
            promise.reject(reason);
            return;
          } else {
            // if fin() is called, then onRejected would be null
            result = onRejected.handle(reason);
          }
        } catch (Exception e) {
          result = e;
        }

        if (result instanceof Promise) {
          ((Promise<O>) result).become(promise);
        } else if (result instanceof Exception) {
          promise.reject((Exception) result);
        } else {
          promise.fulfill((O) result);
        }
      }

      @Override
      @SuppressWarnings("unchecked")
      public void onNext(T value) {
        Object result = null;

        try {
          // Perform any finally handlers
          if (onFinally != null) {
            result = onFinally.handle(null);

            // results from fin() handler is ignored
            if (!(result instanceof Promise)) {
              result = null;
            }
          } else if (onFulfilled == null) {
            // We don't have a handler so we'll just forward on
            // We have to assume that the casting will work...
            promise.fulfill((O) value);
            return;
          } else {
            // if fin() is called, then onFulfilled would be null
            result = onFulfilled.handle(value);
          }
        } catch (Exception e) {
          result = e;
        }

        if (result instanceof Promise) {
          ((Promise<O>) result).become(promise);
        } else if (result instanceof Exception) {
          promise.reject((Exception) result);
        } else {
          promise.fulfill((O) result);
        }
      }
    });

    return promise;
  }

  /* Result Methods */
  public void fulfill(T value) {
    for (Observer<T> obs : this.observers.values()) {
      obs.onNext(value);
      obs.onCompleted();
    }
  }

  public void reject(Exception reason) {
    for (Observer<T> obs : this.observers.values()) {
      obs.onError(new Exception(reason));
    }
  }

  public void become(Promise<T> other) {
    this.observers.putAll(other.observers);
  }
}
