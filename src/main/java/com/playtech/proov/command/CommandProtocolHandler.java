package com.playtech.proov.command;


import com.playtech.proov.server.Connection;
import com.playtech.proov.server.ProtocolHandler;
import com.playtech.proov.server.ServerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Parses serialized {@link Command} implementations and delegates processing to
 * {@link com.playtech.proov.server.ApplicationInvoker}
 */
public class CommandProtocolHandler implements ProtocolHandler {

    private static final Logger LOG = LoggerFactory.getLogger(CommandProtocolHandler.class);
    private ServerContext context;

    public CommandProtocolHandler(ServerContext context) {
        this.context = context;
    }

    /**
     * Expects input to be serialized {@link Command} implementation
     *
     * @param con connection with client
     */
    public void onData(Connection con) {
        byte[] data = con.getReadQueue().poll();
        try {
            Command command = fromBytes(data);

            ServerCommandRequest serverCommandRequest = new ServerCommandRequest(command);
            LOG.debug("ServerCommandRequest {}", command);

            ServerCommandResponse serverCommandResponse = new ServerCommandResponse();
            context.getApplicationInvoker().invokeApplication(serverCommandRequest, serverCommandResponse);

            NetworkCommandResponse networkCommandResponse = new NetworkCommandResponse();
            networkCommandResponse.setServiceResponse(serverCommandResponse.getResponse());

            LOG.debug("Service response {}", networkCommandResponse.getServiceResponse());
            byte[] bytes = toBytes(networkCommandResponse);

            con.write(bytes);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] toBytes(Object obj) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(obj);
        out.write(-1);

        return out.toByteArray();
    }

    private static Command fromBytes(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream is = new ObjectInputStream(in);
        return (Command) is.readObject();
    }
}
