package org.edgeframework.edge.core.api;

import org.edgeframework.edge.core.api.delegates._AppDelegateContainer;

// A - a implementation of _Application
// C - a implementation of _AppDelegateContainer. Must hold and return AppDelegates of A.
public interface _Application<A extends _Application<A, C>, C extends _AppDelegateContainer<A, ?>> {
  C getDelegates();
}
