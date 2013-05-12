package com.playtech.proov.server;


import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServerSession implements Serializable {

    private static final long serialVersionUID = 1;

    private AuthenticationContext authenticationContext;
    private String sessionId;

    private Map<Serializable, Object> sessionParams = new ConcurrentHashMap<>();

    public ServerSession(String sessionId, AuthenticationContext authenticationContext) {
        this.sessionId = sessionId;
        this.authenticationContext = authenticationContext;
    }

    public Map<Serializable, Object> getSessionParams() {
        return Collections.unmodifiableMap(sessionParams);
    }

    public void addSessionParam(String key, String value) {
        sessionParams.put(key, value);
    }

    public AuthenticationContext getAuthenticationContext() {
        return authenticationContext;
    }

    public String getSessionId() {
        return sessionId;
    }

}
