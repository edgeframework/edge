package org.edgeframework.edge.core.groovy.http

import groovy.transform.CompileStatic

import org.edgeframework.edge.core.java.http.HttpRequest
import org.edgeframework.edge.core.java.http.HttpResponse
import org.vertx.groovy.core.Vertx

@CompileStatic
public class HttpContext {
  final Vertx vertx
  Vertx vertx() {
    return this.vertx
  }

  final HttpRequest request
  HttpRequest request() {
    return this.request
  }

  final HttpResponse response
  HttpResponse response() {
    return this.response
  }

  HttpContext(org.vertx.java.core.Vertx jVertex) {
    this.vertx = new Vertx(jVertex)
  }
}