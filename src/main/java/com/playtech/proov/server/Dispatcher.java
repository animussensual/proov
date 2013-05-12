package com.playtech.proov.server;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;

/**
 * Listens read and write requests and delegates these to {@link DispatcherEventHandler}
 */
public class Dispatcher implements Runnable {

    public Object guard = new Object();
    private static final Logger LOG = LoggerFactory.getLogger(Dispatcher.class);
    private Selector selector;
    private DispatcherEventHandler dispatcherEventHandler;

    public Dispatcher() throws IOException {
        this.selector = Selector.open();
        dispatcherEventHandler = new DispatcherEventHandler(10, this);
    }

    void register(Connection con) {
        synchronized (guard) {
            selector.wakeup();
            try {
                con.getChannel().register(selector, SelectionKey.OP_READ, con);
            } catch (ClosedChannelException e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }

    public void deregister(Connection con) throws IOException {
        con.getChannel().close();
    }

    void announceWriteNeed(Connection con) {
        SelectionKey key = con.getChannel().keyFor(selector);
        if (key != null) {
            synchronized (guard) {
                key.interestOps(SelectionKey.OP_WRITE);
                selector.wakeup();
            }
        }
    }

    public void run() {
        while (Server.isRunning) {
            synchronized (guard) {
                // suspend the dispatcher thread if guard is locked
            }
            try {
                selector.select();
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> it = selectionKeys.iterator();
                while (it.hasNext()) {
                    SelectionKey key = it.next();
                    it.remove();

                    Connection con = (Connection) key.attachment();
                    try {
                        if (key.isValid()) {
                            if (key.isReadable()) {
                                dispatcherEventHandler.onReadableEvent(con);
                            }

                            if (key.isWritable()) {
                                dispatcherEventHandler.onWritableEvent(con);
                                key.interestOps(SelectionKey.OP_READ);
                            }
                        }
                    } catch (CancelledKeyException e) {
                        deregister(con);
                    }

                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }

}
