package com.playtech.proov.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Handles read/write events from a {@link Dispatcher}.
 * <p/>
 * Uses {@link Connection} input queue to delegate data processing to
 * the current {@link ProtocolHandler}.
 */
public class DispatcherEventHandler {

    private static final Logger LOG = LoggerFactory.getLogger(DispatcherEventHandler.class);
    private Executor workerPool;
    private Dispatcher dispatcher;

    public DispatcherEventHandler(int threadsCount, Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
        workerPool = Executors.newFixedThreadPool(threadsCount);
    }

    void onReadableEvent(final Connection con) throws IOException {
        ByteBuffer readBuffer = allocateMemory();

        SocketChannel channel = con.getChannel();
        int readCount;
        try {
            readCount = channel.read(readBuffer);
        } catch (IOException e) {
            dispatcher.deregister(con);
            return;
        }

        if (readCount == -1) {
            dispatcher.deregister(con);
            return;
        }

        ByteBuffer data = extractReadAndRecycleRenaming(readBuffer);

        byte[] copy = new byte[readCount];
        System.arraycopy(data.array(), 0, copy, 0, copy.length);
        con.getReadQueue().add(copy);

        if (con.getReadQueue().size() > 0) {
            workerPool.execute(new Runnable() {
                public void run() {
                    synchronized (con) {
                        try {
                            con.getProtocolHandler().onData(con);
                        } catch (Exception e) {
                            LOG.error(e.getMessage(), e);
                        }
                    }
                }
            });
        }
    }


    void onWritableEvent(Connection con) throws IOException {
        byte[] data = con.getWriteQueue().poll();
        if (data != null) {
            System.out.printf("Server send data %s.%n", new String(data));
            con.getChannel().write(ByteBuffer.wrap(data));
        }

        if (!con.getWriteQueue().isEmpty()) {
            dispatcher.announceWriteNeed(con);
        }
    }

    private ByteBuffer extractReadAndRecycleRenaming(ByteBuffer readBuffer) {
        readBuffer.flip();
        return readBuffer;
    }

    private ByteBuffer allocateMemory() {
        return ByteBuffer.allocate(1024);
    }

}
