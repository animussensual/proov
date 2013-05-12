package com.playtech.proov.server;


import com.playtech.proov.util.ConfigurationLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Properties;

/**
 * Entry point to server.
 * <p/>
 * Handles connection requests and delegates processing to a {@link Dispatcher}.
 */
public class Acceptor implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(Acceptor.class);
    private ProtocolHandler protocolHandler;
    private DispatcherPool dispatcherPool;
    private ServerSocketChannel serverChannel;

    void init(ServerContext serverContext) throws IOException {
        Properties properties = ConfigurationLoader.getProperties(Server.SERVER_PROPERTIES);

        serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(true);

        int serverPort = Integer.parseInt(properties.getProperty("server.port"));
        serverChannel.socket().bind(new InetSocketAddress(serverPort));

        dispatcherPool = new DispatcherPool(10);
        dispatcherPool.startDispatchers();

        protocolHandler = serverContext.getProtocolHandler();

        LOG.info("Listening connections on {}", serverPort);
    }

    public void run() {
        while (Server.isRunning) {
            try {
                SocketChannel channel = serverChannel.accept();
                channel.configureBlocking(false);
                Dispatcher dispatcher = dispatcherPool.nextDispatcher();
                Connection con = new Connection(channel, protocolHandler, dispatcher);
                InetSocketAddress remoteAddress = (InetSocketAddress) con.getChannel().getRemoteAddress();

                LOG.info("Register new connection from {}", remoteAddress.getHostString());
                dispatcher.register(con);
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }


}
