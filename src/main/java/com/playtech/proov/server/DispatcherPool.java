package com.playtech.proov.server;


import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class DispatcherPool {

    private final List<Dispatcher> dispatchers = new CopyOnWriteArrayList<>();

    public DispatcherPool(int poolSize) throws IOException {
        for (int i = 0; i < poolSize; i++) {
            Dispatcher dispatcher = new Dispatcher();
            dispatchers.add(dispatcher);
        }
    }

    public void startDispatchers() {
        for (Dispatcher dispatcher : dispatchers) {
            Thread thread = new Thread(dispatcher);
            thread.start();
        }
    }

    public Dispatcher nextDispatcher() {
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        return dispatchers.get(rnd.nextInt(0, dispatchers.size()));
    }
}
