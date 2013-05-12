package com.playtech.proov.server;


/**
 * Handler which knows how to parse specific protocol request data.
 */
public interface ProtocolHandler {

    /**
     * Parses input to server
     *
     * @param con connection from a client
     */
    public void onData(Connection con);

}
