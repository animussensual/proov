package com.playtech.proov.command;

import java.io.Serializable;

/**
 * Represents response from remote service invocation
 */
public class NetworkCommandResponse implements Serializable {

    private int status;
    private Object serviceResponse;

    public Object getServiceResponse() {
        return serviceResponse;
    }

    public void setServiceResponse(Object serviceResponse) {
        this.serviceResponse = serviceResponse;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
