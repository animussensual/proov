package com.playtech.proov.server.auth;


import com.playtech.proov.command.CommandProtocolHandler;
import com.playtech.proov.command.ServerCommandRequest;
import com.playtech.proov.http.HttpProtocolHandler;
import com.playtech.proov.http.HttpRequest;
import com.playtech.proov.server.AuthenticationContext;
import com.playtech.proov.server.ProtocolHandler;
import com.playtech.proov.server.ServerContext;
import com.playtech.proov.server.ServerRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles authentication context creation based on server protocol
 */
public class AuthenticationProvider {

    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationProvider.class);
    private final ServerContext serverContext;

    public AuthenticationProvider(ServerContext serverContext) {
        this.serverContext = serverContext;
    }

    public AuthenticationContext getAuthenticationContext(ServerRequest serverRequest) {
        AuthenticationContext authenticationContext = new AuthenticationContext();
        ProtocolHandler protocolHandler = serverContext.getProtocolHandler();

        if (HttpProtocolHandler.class.equals(protocolHandler.getClass())) {
            HttpRequest httpRequest = (HttpRequest) serverRequest;

            String userName = httpRequest.getParam("userName");
            authenticationContext.setUserName(userName);

        } else if (CommandProtocolHandler.class.equals(protocolHandler.getClass())) {
            ServerCommandRequest serverCommandRequest = (ServerCommandRequest) serverRequest;
            authenticationContext.setUserName(serverCommandRequest.getCommand().getUserName());
        }

        return authenticationContext;
    }
}
