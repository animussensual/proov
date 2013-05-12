package com.playtech.proov.server.management;


public interface StatisticsMXBean {

    long getAverageTime();

    long getMaxTime();

    long getMinTime();

    long getNumberOfQueries();
}
