package com.playtech.proov.server;

import com.playtech.proov.ServerApplication;
import com.playtech.proov.annotations.Application;
import com.playtech.proov.http.HttpProtocolHandler;
import com.playtech.proov.server.auth.AuthenticationProvider;
import com.playtech.proov.services.ServicesPool;
import com.playtech.proov.util.ConfigurationLoader;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;

/**
 * Contains components for a running server instance
 */
public class ServerContext {

    private static final Logger LOG = LoggerFactory.getLogger(ServerContext.class);

    private ProtocolHandler protocolHandler;
    private ApplicationInvoker applicationInvoker;
    private AuthenticationProvider authenticationProvider;
    private Map<String, ServerApplication> applicationMap = new ConcurrentHashMap<>();
    private Map<String, Map<String, ServerSession>> serverSessions = new ConcurrentHashMap<>();

    public ServerContext() {
        Properties serverProperties = ConfigurationLoader.getProperties(Server.SERVER_PROPERTIES);

        authenticationProvider = new AuthenticationProvider(this);
        this.applicationInvoker = new ApplicationInvoker(this);

        Class protocolHandlerClass = HttpProtocolHandler.class;

        if (serverProperties.containsKey("server.protocol.handler")) {
            try {
                String protocolHandler = serverProperties.getProperty("server.protocol.handler");
                protocolHandlerClass = Class.forName(protocolHandler);
            } catch (ClassNotFoundException e) {
                LOG.error("Invalid protocolHandler class {}, using default", protocolHandler);
            }
        }

        try {
            LOG.info("Using protocol handler {}", protocolHandlerClass);
            this.protocolHandler = (ProtocolHandler) protocolHandlerClass.getConstructor(ServerContext.class).newInstance(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        String applications = serverProperties.getProperty("applications");
        String appsDir = serverProperties.getProperty("applications.dir");
        initApplications(appsDir, applications);
    }


    /**
     * Based on applications root directory and list of packages finds
     * {@link Application} and {@link Resource} annotated classes to initialize
     * applications and related services.
     * <p/>
     * Each {@link Application} annotated class is handled as an application entry point
     * with specific path relative to domain. It has to also implement {@link ServerApplication}
     * interface.
     * <p/>
     * Each {@link Resource} annotated class represents a service and their instances are kept
     * in {@link ServicesPool}
     *
     * @param appRoot      directory where search classes
     * @param applications specific packages to scan
     */
    void initApplications(String appRoot, String applications) {
        LOG.info("Initialize applications");

        final String[] apps = applications.split(",");

        Path appsRoot = Paths.get(appRoot);
        LOG.info("Applications root is {}", appsRoot.toAbsolutePath());

        final File file = appsRoot.toFile();
        if (file.isDirectory()) {

            File[] dirs = file.listFiles(new FileFilter() {

                @Override
                public boolean accept(File dir) {

                    boolean isAppDir = dir.isDirectory() && ArrayUtils.contains(apps, dir.getName());
                    if (isAppDir) {
                        LOG.info("{} is application directory.", dir);
                    }
                    return isAppDir;
                }
            });

            final PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:*.{class}");

            for (final File appDir : dirs) {
                try {
                    final ServicesPool servicesPool = new ServicesPool();
                    Files.walkFileTree(appDir.toPath(), new SimpleFileVisitor<Path>() {

                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                            if (matcher.matches(file.getFileName())) {

                                String className = findClassName(file);
                                try {
                                    Class<?> aClass = Class.forName(className);

                                    Application annotation = aClass.getAnnotation(Application.class);
                                    if (annotation != null) {
                                        initializeApplication(className, aClass, annotation);
                                    }

                                    Resource resource = aClass.getAnnotation(Resource.class);
                                    if (resource != null) {
                                        LOG.info("Initialize service {} ", className);
                                        Object service = aClass.newInstance();
                                        Class<?> sInterface = aClass.getInterfaces()[0];//Simple dirty solution
                                        servicesPool.addService(sInterface, service);
                                    }

                                } catch (Exception e) {
                                    LOG.error(e.getMessage(), e);
                                }
                            }
                            return FileVisitResult.CONTINUE;
                        }

                        private String findClassName(Path file) {
                            String path = file.toString();
                            int classes = path.indexOf("classes");
                            int packageStart = classes + "classes".length() + 1;
                            String className = path.substring(packageStart, path.length());
                            String separator = Matcher.quoteReplacement(File.separator);
                            className = className.replaceAll(separator, ".").replace(".class", "");
                            return className;
                        }

                        private void initializeApplication(String className, Class<?> aClass,
                                                           Application annotation) throws Exception {

                            String appPath = annotation.path();
                            ServerApplication application = (ServerApplication) aClass.
                                    getConstructor(ServicesPool.class).newInstance(servicesPool);

                            LOG.info("Found server application class {}", className);
                            LOG.info("Application path is {}", appPath);

                            applicationMap.put(appPath, application);
                        }
                    });

                } catch (IOException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        }
    }

    /**
     * Binds request with session. Creates new session if no required session exists.
     *
     * @param request active client request
     */
    public void bindSession(ServerRequest request) {
        String path = request.getPath();
        if (path == null) {
            path = "/";
        }

        Map<String, ServerSession> appSessions = serverSessions.get(path);

        if (appSessions == null) {
            appSessions = new ConcurrentHashMap<>();
            serverSessions.put(path, appSessions);
        }

        String sessionId = request.getSessionId();
        if (sessionId == null) {
            sessionId = "guest_" + UUID.randomUUID().toString();
        }

        LOG.debug("Bind session with id {}", sessionId);

        ServerSession serverSession = appSessions.get(sessionId);

        if (serverSession == null) {
            LOG.debug("Create new session for with id {}", sessionId);
            AuthenticationContext authenticationContext = authenticationProvider.getAuthenticationContext(request);
            serverSession = new ServerSession(sessionId, authenticationContext);
            appSessions.put(sessionId, serverSession);
        }

        request.setServerSession(serverSession);

    }

    /**
     * Finds application which is registered for request path
     *
     * @param request active request
     * @return application for the request path
     */
    public ServerApplication resolvePath(ServerRequest request) {
        String path = request.getPath();
        if (path == null) {
            path = "/";
        }
        ServerApplication serverApplication = applicationMap.get(path);
        if (serverApplication == null) {
            throw new RuntimeException("Path " + path + " didn't resolve to any serverApplication");
        } else {
            return serverApplication;
        }
    }

    public ApplicationInvoker getApplicationInvoker() {
        return applicationInvoker;
    }

    public ProtocolHandler getProtocolHandler() {
        return protocolHandler;
    }

}
