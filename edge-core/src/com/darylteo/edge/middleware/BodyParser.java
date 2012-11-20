package com.darylteo.edge.middleware;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.handler.codec.http.multipart.Attribute;
import org.jboss.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import org.jboss.netty.handler.codec.http.multipart.DiskAttribute;
import org.jboss.netty.handler.codec.http.multipart.DiskFileUpload;
import org.jboss.netty.handler.codec.http.multipart.FileUpload;
import org.jboss.netty.handler.codec.http.multipart.HttpDataFactory;
import org.jboss.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import org.jboss.netty.handler.codec.http.multipart.InterfaceHttpData;
import org.jboss.netty.handler.codec.http.multipart.InterfaceHttpData.HttpDataType;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.deploy.impl.VertxLocator;

import com.darylteo.edge.core.requests.EdgeHandler;
import com.darylteo.edge.core.requests.EdgeRequest;
import com.darylteo.edge.core.requests.EdgeResponse;

public class BodyParser extends EdgeHandler {

  static {
    DiskFileUpload.baseDirectory = null;
    DiskFileUpload.deleteOnExitTemporaryFile = true;
    DiskAttribute.baseDirectory = null;
    DiskAttribute.deleteOnExitTemporaryFile = true;
  }

  // Disk if size exceed MINSIZE
  private static final HttpDataFactory factory = new DefaultHttpDataFactory(false);

  @Override
  public void handleRequest(final EdgeRequest request, final EdgeResponse response) {

    /* Ignore methods without request body */
    String method = request.getMethod().toLowerCase();
    if (!method.equalsIgnoreCase("post") &&
        !method.equalsIgnoreCase("patch") &&
        !method.equalsIgnoreCase("put")) {
      next();
      return;
    }

    /* Check ContentType is Url Encoded or Multipart Form-Data */
    String contentType = request.getHeader(HttpHeaders.Names.CONTENT_TYPE);
    if (contentType == null) {
      return;
    }

    contentType = contentType.toLowerCase();
    if (!contentType.startsWith(HttpHeaders.Values.APPLICATION_X_WWW_FORM_URLENCODED) &&
        !contentType.startsWith(HttpHeaders.Values.MULTIPART_FORM_DATA)) {
      return;
    }

    /* Attempt to Parse */
    try {
      /* Create a dummy Netty Request with the body */
      final HttpServerRequest vertxReq = request.getUnderlyingRequest();
      final HttpRequest nettyReq = new DefaultHttpRequest(HttpVersion.HTTP_1_1, new HttpMethod(vertxReq.method), vertxReq.uri);

      nettyReq.setChunked(false);
      nettyReq.setContent(ChannelBuffers.wrappedBuffer(request.getRawBody()));

      for (Map.Entry<String, String> header : vertxReq.headers().entrySet()) {
        System.out.println(header);
        nettyReq.addHeader(header.getKey(), header.getValue());
      }

      final HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(BodyParser.factory, nettyReq);

      for (InterfaceHttpData data : decoder.getBodyHttpDatas()) {
        HttpDataType type = data.getHttpDataType();

        if (type == HttpDataType.Attribute) {

          Attribute attribute = (Attribute) data;
          System.out.println(attribute.getName());
          System.out.println(attribute.getValue());

          Map<String, Object> body = request.getBody();
          String key = attribute.getName();

          Object value = body.get(key);
          if (value == null) {
            body.put(key, attribute.getValue());
          } else {
            if (!(value instanceof List<?>)) {
              List<Object> list = new LinkedList<>();
              list.add(value);

              body.put(key, list);
              value = list;
            }

            ((List<Object>) value).add(attribute.getValue());
          }

        } else if (type == HttpDataType.FileUpload) {

          FileUpload file = (FileUpload) data;
          System.out.println(file.getName());

          if (!file.isCompleted() || file.length() == 0) {
            return;
          }

          /* Factory specifies in-memory file store */
          byte[] fileContents = file.get();

          request.getBody().put(file.getName(), file.getFilename());
          request.getFiles().put(file.getName(), fileContents);

        } else {
          System.out.println(data);
        }
      }

    } catch (Exception e) {
      VertxLocator.container.getLogger().error("Error in BodyParser", e);
    } finally {
      next();
    }
  }
}
