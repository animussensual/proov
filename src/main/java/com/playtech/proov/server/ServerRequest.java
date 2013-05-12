package com.playtech.proov.server;


import java.io.Serializable;

/**
 * Contains translated request data for applications.
 */
public abstract class ServerRequest implements Serializable {

    private ServerSession serverSession;
    private String sessionId;
    private String host;
    private String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public ServerSession getServerSession() {
        return serverSession;
    }

    public String getSessionId() {
        return sessionId;
    }

    void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    void setServerSession(ServerSession serverSession) {
        this.serverSession = serverSession;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public String toString() {
        return "ServerRequest{" +
                "host='" + host + '\'' +
                ", serverSession=" + serverSession +
                ", sessionId='" + sessionId + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
