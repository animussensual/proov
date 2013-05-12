package com.playtech.proov.http;


import com.playtech.proov.server.Connection;
import com.playtech.proov.server.ServerResponse;

/**
 * Server response for {@link com.playtech.proov.http.HttpProtocolHandler}
 */
public class HttpResponse implements ServerResponse {

    private Connection connection;

    private StringBuilder buffer = new StringBuilder();

    public HttpResponse(Connection connection) {
        this.connection = connection;
    }

    public void write(String data) {
        buffer.append(data);
    }

    public StringBuilder getBuffer() {
        return buffer;
    }
}
