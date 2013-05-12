package com.playtech.proov.server.management;


import com.playtech.proov.server.ServerRequest;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Calculates statistics based on events
 */
public class StatisticsCollector implements Runnable {

    private Set<Event> events = new ConcurrentSkipListSet<>();
    private Statistics latest = new Statistics();
    private ReadWriteLock lock = new ReentrantReadWriteLock();

    void collect(ServerRequest request, long elapsed) {
        events.add(new Event(elapsed, request));
    }

    public StatisticsMXBean getLatest() {
        try {
            lock.readLock().lock();
            return latest;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(60 * 1000);
                collectStatistics();
            } catch (InterruptedException e) {
            }
        }
    }

    private void collectStatistics() {
        Set<Event> copy = new HashSet<>(events);
        events.clear();
        Statistics statistics = new Statistics();
        statistics.setNumberOfQueries(copy.size());
        long min = Long.MAX_VALUE;
        long max = Long.MIN_VALUE;
        long sum = 0;
        for (Event event : copy) {
            long elapsed = event.getElapsed();
            sum += elapsed;
            if (min > elapsed) {
                min = elapsed;
            }
            if (max < elapsed) {
                max = elapsed;
            }
        }
        statistics.setMinTime(min);
        statistics.setMaxTime(max);
        statistics.setAverageTime(sum / copy.size());
        statistics.setSum(sum);

        try {
            lock.writeLock().lock();

            latest.merge(statistics);
        } finally {
            lock.writeLock().unlock();
        }
    }

    private class Event implements Comparable<Event> {

        private ServerRequest request;
        private long elapsed;

        private Event(long elapsed, ServerRequest request) {
            this.elapsed = elapsed;
            this.request = request;
        }

        private long getElapsed() {
            return elapsed;
        }

        private ServerRequest getRequest() {
            return request;
        }

        private void setRequest(ServerRequest request) {
            this.request = request;
        }

        @Override
        public int compareTo(Event o) {
            return (int) (o.getElapsed() - elapsed);
        }
    }

}
