package com.playtech.proov.command;

import com.playtech.proov.server.ServerResponse;


/**
 * Server response for {@link CommandProtocolHandler}
 */
public class ServerCommandResponse implements ServerResponse {

    private static final long serialVersionUID = 1;

    private Object response;

    public void setResponse(Object response) {
        this.response = response;
    }

    public Object getResponse() {
        return response;
    }
}
