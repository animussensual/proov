package com.playtech.proov;


import com.playtech.proov.command.Command;
import com.playtech.proov.command.NetworkCommandResponse;
import com.playtech.proov.command.MethodInvokerCommand;
import com.playtech.proov.server.ApplicationInvoker;
import com.playtech.proov.server.ServerSession;
import com.playtech.proov.services.wallet.WalletService;
import com.playtech.proov.util.ConfigurationLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Provides access to remote services through proxy.
 */
public class RemoteServicesFactory {

    private static final Logger LOG = LoggerFactory.getLogger(RemoteServicesFactory.class);
    private final Properties serviceProperties;
    private static final RemoteServicesFactory factory = new RemoteServicesFactory();
    private ConcurrentHashMap<Class, Object> services = new ConcurrentHashMap<>();

    private RemoteServicesFactory() {
        LOG.info("Initializing services factory...");

        serviceProperties = ConfigurationLoader.getProperties("service.properties");

        WalletService walletservice = getRemoteService(WalletService.class);
        services.put(WalletService.class, walletservice);

        LOG.info("Initializing servicefactory initialized");
    }

    public <T> T getRemoteService(Class<T> service) {
        return (T) Proxy.newProxyInstance(service.getClassLoader(),
                new Class[]{service},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                        Socket socket = new Socket();
                        socket.setTcpNoDelay(true);

                        String host = serviceProperties.getProperty("wallet.service.host");

                        int port = Integer.parseInt(serviceProperties.getProperty("wallet.service.port"));
                        LOG.debug("Service host is {} and port is {}", host, port);

                        socket.connect(new InetSocketAddress(host, port));

                        Command command = new MethodInvokerCommand();
                        ServerSession serverSession = ApplicationInvoker.getServerSession();
                        command.setUserName(serverSession.getAuthenticationContext().getUserName());
                        command.setClassName(WalletService.class.getName());
                        command.setMethodName(method.getName());

                        command.setArgumentTypes(method.getParameterTypes());
                        command.setArguments(args);

                        OutputStream outputStream = socket.getOutputStream();
                        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
                        ObjectOutputStream os = new ObjectOutputStream(bufferedOutputStream);
                        os.writeObject(command);
                        os.flush();

                        ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                        NetworkCommandResponse response = (NetworkCommandResponse) objectInputStream.readObject();
                        LOG.debug("Result is {}", response);
                        return response.getServiceResponse();
                    }
                });
    }

    public static RemoteServicesFactory getFactory() {
        return factory;
    }
}
