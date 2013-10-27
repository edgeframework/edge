package org.edgeframework.java.core.controller;

import java.util.Map;

import org.edgeframework.java.core.Edge;
import org.edgeframework.java.core.http.Request;
import org.edgeframework.java.core.http.Response;
import org.vertx.java.core.Vertx;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public abstract class Controller {

  /* Persistence Strategies */
  public static abstract class PersistenceStrategy {
    private Map<String, String> existing;

    private Map<String, String> getMap() {
      if (existing != null) {
        return existing;
      }
      existing = createMap();
      return existing;
    }

    abstract Map<String, String> createMap();
  }

  public class Persistence {
    public final PersistenceStrategy IN_MEMORY = new PersistenceStrategy() {
      @Override
      public Map<String, String> createMap() {
        return vertx.sharedData().getMap("edge:session");
      }
    };
  }

  /* Instance Variables */
  private Vertx vertx;
  private Edge edge;
  private Request request;

  private Persistence strategies = new Persistence();
  private PersistenceStrategy sessionStrategy = strategies.IN_MEMORY;

  void setRequest(Request request) {
    this.request = request;
  }

  void setEdge(Edge edge) {
    this.edge = edge;
  }

  void setVertx(Vertx vertx) {
    this.vertx = vertx;
  }

  /* Util Classes */
  protected abstract class Result {
    public abstract void render(Response response);
  }

  /* Properties */
  protected Request request() {
    return this.request;
  }

  protected String beginSession() {
    // String sessionID = request.getSessionId();
    //
    // if (sessionID == null) {
    // sessionID = UUID.randomUUID().toString();
    // request.setSessionId(sessionID);
    // }
    //
    // return sessionID;
    throw new NotImplementedException();
  }

  protected void endSession() {
    // String sessionID = request.getSessionId();
    //
    // if (sessionID == null) {
    // return;
    // }
    //
    // // TODO: remove persistence backend
    // request.setSessionId(null);
    throw new NotImplementedException();
  }

  protected boolean isActiveSession() {
    // return request.getSessionId() != null;
    throw new NotImplementedException();
  }

  protected Map<String, String> session() {
    // // TODO: I'm going to rely heavily on the Yoke sessionID implementation
    // for
    // // this
    // // If there are any significant security/auth leaking issues then we may
    // // relook this.
    // String sessionID = request.getSessionId();
    //
    // if (sessionID == null) {
    // // TODO: provide warning about accessing map
    // return Collections.emptyMap();
    // }
    //
    // // TODO: implement different types of session persistence strategies
    //
    // // return the session map for this session
    // // if there isn't one, then create one
    // return this.sessionStrategy.getMap();
    throw new NotImplementedException();
  }

  protected String cookies(String name) {
    // List<YokeCookie> cookies = request.getAllCookies(name);
    //
    // if (cookies != null) {
    // for (YokeCookie cookie : cookies) {
    // if (Objects.equals(cookie.getName(), name)) {
    // return cookie.getValue();
    // }
    // }
    // }
    //
    // return null;
    throw new NotImplementedException();
  }

  protected void cookies(String name, String value) {
    // YokeCookie cookie = new YokeCookie(name, this.face.getMac());
    // cookie.setValue(value);
    // request.response().addCookie(cookie);
    throw new NotImplementedException();
  }

  /* Result Methods */
  protected Result ok(final String content) {
    return new Result() {
      @Override
      public void render(Response response) {
        response.setStatusCode(200);
        response.header("content-length", content.length());
        response.write(content);
        response.close();
      }
    };
  }
}
