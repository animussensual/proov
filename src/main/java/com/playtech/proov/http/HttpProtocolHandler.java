package com.playtech.proov.http;


import com.playtech.proov.server.Connection;
import com.playtech.proov.server.ProtocolHandler;
import com.playtech.proov.server.ServerContext;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Knows how to parse HTTP requests.
 * Supports only GET requests.
 * <p/>
 * Delegates processing to * {@link com.playtech.proov.server.ApplicationInvoker}.
 */
public class HttpProtocolHandler implements ProtocolHandler {

    private static final Logger LOG = LoggerFactory.getLogger(HttpProtocolHandler.class);
    private static final String LS = "\r\n";
    private ServerContext context;

    public HttpProtocolHandler(ServerContext context) {
        this.context = context;
    }

    @Override
    public void onData(Connection con) {

        byte[] data = con.getReadQueue().poll();

        if (LOG.isDebugEnabled()) {
            LOG.debug("Got request {}", new String(data));
        }

        Scanner scanner = new Scanner(new String(data, Charset.forName("UTF8")));
        scanner.useDelimiter(System.lineSeparator());

        HttpRequest request = new HttpRequest();

        //Request line like GET /?userName=andrus&balance=1000 HTTP/1.1
        if (scanner.hasNext()) {
            String requestLine = scanner.next();
            String[] split = requestLine.split(" ");

            request.setMethod(split[0]);

            String fullPath = split[1];
            int i = fullPath.indexOf("?");
            if (i == -1) {
                i = fullPath.length();
            } else {
                String query = fullPath.substring(i + 1, fullPath.length());
                request.setParams(parseParams(query, "&"));
            }

            request.setPath(fullPath.substring(0, i));


        } else {
            //error
        }
        //Host like Host: localhost:9999
        if (scanner.hasNext()) {
            String host = scanner.next();
            String[] split = host.split(": ");

            //with port
            if (split.length == 2) {
                request.setHost(split[1]);
            }
        }

        //parse headers and cookies
        while (scanner.hasNext()) {
            String line = scanner.next();
            if (StringUtils.isBlank(line)) {
                break;
            }
            line = line.trim();
            String[] split = line.split(":");
            if (split.length == 2) {
                String key = split[0].trim();
                String value = split[1].trim();
                if (key.equals("Cookie")) {
                    request.setCookies(parseParams(value, ":"));
                } else {
                    request.addHeader(key, value);
                }

            }
        }

        LOG.debug("HttpRequest {}", request);

        String accept = request.getHeader("Accept");
        HttpResponse response = new HttpResponse(con);
        if (StringUtils.isNotEmpty(accept) && accept.contains("text/html")) {
            try {
                context.getApplicationInvoker().invokeApplication(request, response);
            } catch (Exception e) {
                sendError(response, ExceptionUtils.getStackTrace(e));
            }
        } else {
            sendError(response, "Unknown request");
        }

        sendResponse(con, response);

    }

    private void sendError(HttpResponse response, String error) {
        response.write(error);
    }

    private void sendResponse(Connection connection, HttpResponse response) {
        String responseString = "HTTP/1.1 200 OK" + LS;
        StringBuilder buffer = response.getBuffer();
        responseString += "Content-Length:" + buffer.length() + LS;
        responseString += LS;
        responseString += buffer.toString() + LS + LS + LS;

        connection.write(responseString);
    }

    private Map<String, String> parseParams(String value, String separator) {
        Map<String, String> params = new HashMap<>();
        String[] split = value.split(separator);
        for (String param : split) {
            String[] kv = param.split("=");
            if (kv.length == 2) {
                params.put(kv[0].trim(), kv[1]);
            }
        }
        return params;
    }
}

