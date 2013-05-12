package com.playtech.proov.http;


import com.playtech.proov.server.ServerRequest;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Server request for {@link com.playtech.proov.http.HttpProtocolHandler}
 */
public class HttpRequest extends ServerRequest {

    private String method;
    private Map<String, String> headers = new HashMap<>();
    private Map<String, String> cookies = new HashMap<>();
    private Map<String, String> params = new HashMap<>();

    @Override
    public String getSessionId() {
        //Primitive session mapping
        return getParam("userName");
    }

    public String getMethod() {
        return method;
    }

    public Map<String, String> getParams() {
        return Collections.unmodifiableMap(params);
    }

    public String getParam(String name) {
        return params.get(name);
    }

    void setMethod(String method) {
        this.method = method;
    }


    public Map<String, String> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }


    void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public Map<String, String> getCookies() {
        return Collections.unmodifiableMap(cookies);
    }

    void setCookies(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    void addCookie(String key, String value) {
        headers.put(key, value);
    }


    void setParams(Map<String, String> params) {
        this.params = params;
    }

    public String getHeader(String header) {
        return headers.get(header);
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "cookies=" + cookies +
                ", method='" + method + '\'' +
                ", headers=" + headers +
                ", params=" + params +
                '}';
    }


}
