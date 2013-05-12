package com.playtech.proov.server;


import java.nio.channels.SocketChannel;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Represents client-server connection with corresponding channel
 * and input/output queues.
 */
public class Connection {

    private SocketChannel channel;
    private ProtocolHandler appProtocolHandler;
    private Dispatcher dispatcher;
    private Queue<byte[]> readQueue;
    private Queue<byte[]> writeQueue;

    public Connection(SocketChannel channel, ProtocolHandler appProtocolHandler, Dispatcher dispatcher) {
        this.channel = channel;
        this.appProtocolHandler = appProtocolHandler;
        this.dispatcher = dispatcher;
        readQueue = new ConcurrentLinkedQueue<>();
        writeQueue = new ConcurrentLinkedQueue<>();
    }

    public SocketChannel getChannel() {
        return channel;
    }

    public Queue<byte[]> getReadQueue() {
        return readQueue;
    }

    public ProtocolHandler getProtocolHandler() {
        return appProtocolHandler;
    }

    public Dispatcher getDispatcher() {
        return dispatcher;
    }

    public Queue<byte[]> getWriteQueue() {
        return writeQueue;
    }

    public void write(byte[] bytes) {
        writeQueue.add(bytes);
        dispatcher.announceWriteNeed(this);
    }

    public void write(String hoo) {
        byte[] bytes = hoo.getBytes();
        writeQueue.add(bytes);
        dispatcher.announceWriteNeed(this);
    }
}
