package com.playtech.proov.server;


import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Entry point for starting server.
 * <p/>
 * It launches all other components to service requests.
 */
public class Server {

    public static String SERVER_PROPERTIES = "server.properties";
    //Currently unused, JVM is simply killed
    public static volatile boolean isRunning = true;
    private static final Logger LOG = LoggerFactory.getLogger(Server.class);

    private ServerContext serverContext;

    /**
     * Launches server JVM and takes optional parameter for properties file name
     *
     * @param args JVM args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        Server server = new Server();
        String props = args.length > 0 ? args[0] : null;
        server.start(props);
    }


    public void start(String properties) throws IOException {
        LOG.info("Starting server...");

        if (StringUtils.isNotBlank(properties)) {
            SERVER_PROPERTIES = properties;
        }
        LOG.info("Using server properties {}", SERVER_PROPERTIES);

        serverContext = new ServerContext();

        Acceptor acceptor = new Acceptor();
        acceptor.init(serverContext);

        Thread acceptorThread = new Thread(acceptor);
        acceptorThread.start();

        LOG.info("Server is ready for connections");
    }

}