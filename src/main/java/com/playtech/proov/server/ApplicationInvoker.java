package com.playtech.proov.server;


import com.playtech.proov.ServerApplication;
import com.playtech.proov.server.management.RequestStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Knows how to invoke specific application.
 */
public class ApplicationInvoker {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationInvoker.class);
    private final RequestStatistics statistics;
    private ServerContext serverContext;
    private static ThreadLocal<ServerSession> serverSessionThreadLocal = new ThreadLocal<>();

    public ApplicationInvoker(ServerContext serverContext) {
        this.serverContext = serverContext;
        statistics = new RequestStatistics();
        try {
            statistics.init();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    public void invokeApplication(ServerRequest request, ServerResponse response) {

        ServerApplication serverApplication = serverContext.resolvePath(request);
        serverContext.bindSession(request);


        try {
            ServerSession serverSession = request.getServerSession();
            serverSessionThreadLocal.set(serverSession);

            LOG.debug("{} handles request {}", serverApplication.getClass().getName(), request.getPath());
            LOG.info("IN {}/{}/{}", serverSession.getAuthenticationContext().getUserName(), serverSession.getSessionId(), request.getPath());

            statistics.start(request);

            serverApplication.handleRequestResponse(request, response);

            statistics.end(request);

            LOG.info("OUT {}/{}/{}/{}", serverSession.getAuthenticationContext().getUserName(),
                    serverSession.getSessionId(), request.getPath(), response);

        } finally {
            serverSessionThreadLocal.set(null);
        }
    }

    public static ServerSession getServerSession() {
        return serverSessionThreadLocal.get();
    }

}
