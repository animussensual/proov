package com.playtech.proov.command;

import com.playtech.proov.server.ServerRequest;

/**
 * Server request for {@link CommandProtocolHandler}
 */
public class ServerCommandRequest extends ServerRequest {

    private static final long serialVersionUID = 1;

    private Command command;

    public ServerCommandRequest(Command command) {
        this.command = command;
    }

    public Command getCommand() {
        return command;
    }

    @Override
    public String getPath() {
        return "/";
    }

    @Override
    public String toString() {
        return "ServerCommandRequest{" +
                "command=" + command +
                '}';
    }
}
