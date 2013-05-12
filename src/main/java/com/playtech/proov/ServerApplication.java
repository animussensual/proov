package com.playtech.proov;


import com.playtech.proov.server.ServerRequest;
import com.playtech.proov.server.ServerResponse;

/**
 * Entry point to an application
 */
public interface ServerApplication {

    void handleRequestResponse(ServerRequest request, ServerResponse response);
}
