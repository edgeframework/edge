package org.edgeframework.edge.core.api;

import org.edgeframework.edge.core.api.delegates._AppDelegate;
import org.edgeframework.edge.core.api.delegates._AppDelegateContainer;

public interface _Application<A extends _Application<A, C, AD>, C extends _AppDelegateContainer<A, AD>, AD extends _AppDelegate<A>> {
  C getDelegates();
}
