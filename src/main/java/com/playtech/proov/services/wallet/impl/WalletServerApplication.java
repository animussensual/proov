package com.playtech.proov.services.wallet.impl;

import com.playtech.proov.ServerApplication;
import com.playtech.proov.annotations.Application;
import com.playtech.proov.command.Command;
import com.playtech.proov.command.ServerCommandRequest;
import com.playtech.proov.command.ServerCommandResponse;
import com.playtech.proov.server.ServerRequest;
import com.playtech.proov.server.ServerResponse;
import com.playtech.proov.services.ServicesPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * Entry point to wallet application.
 * <p/>
 * Invokes services based on input {@link Command}
 */
@Application
public class WalletServerApplication implements ServerApplication {

    private static final Logger LOG = LoggerFactory.getLogger(WalletServerApplication.class);
    private final ServicesPool servicesPool;

    public WalletServerApplication(ServicesPool servicesPool) {
        this.servicesPool = servicesPool;
    }

    @Override
    public void handleRequestResponse(ServerRequest request, ServerResponse response) {
        ServerCommandRequest serverCommandRequest = (ServerCommandRequest) request;
        ServerCommandResponse serverCommandResponse = (ServerCommandResponse) response;

        Command command = serverCommandRequest.getCommand();
        try {
            Object service = servicesPool.getService(Class.forName(command.getClassName()));
            Method method = service.getClass().getMethod(command.getMethodName(), command.getArgumentTypes());

            LOG.debug("Invoke service method {}", command);
            Object result = method.invoke(service, command.getArguments());
            serverCommandResponse.setResponse(result);

        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }
}
