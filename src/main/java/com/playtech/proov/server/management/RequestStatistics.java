package com.playtech.proov.server.management;


import com.playtech.proov.server.ServerRequest;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages statistics for server requests.
 * <p/>
 * Registers collected data to JMX server.
 */
public class RequestStatistics {

    private Map<ServerRequest, Long> requests = new ConcurrentHashMap<>();
    private StatisticsCollector collector;


    public void init() throws Exception {
        collector = new StatisticsCollector();

        MBeanServer platformMBeanServer = ManagementFactory.getPlatformMBeanServer();
        ObjectName objectName = new ObjectName("com.playtech.proov:type=statistics,name=RequestStatistics");
        platformMBeanServer.registerMBean(collector.getLatest(), objectName);

        Thread thread = new Thread(collector);
        thread.setDaemon(true);
        thread.start();

    }

    public void start(ServerRequest serverRequest) {
        requests.put(serverRequest, System.nanoTime());
    }

    public void end(ServerRequest serverRequest) {
        Long start = requests.remove(serverRequest);
        long elapsed = System.nanoTime() - start;
        collector.collect(serverRequest, elapsed);
    }

}
