package com.playtech.proov.server.management;


public class Statistics implements StatisticsMXBean {

    private long numberOfQueries;
    private long averageTime;
    private long maxTime;
    private long minTime;
    private long sum;

    void merge(Statistics statistics) {
        setNumberOfQueries(numberOfQueries + statistics.getNumberOfQueries());
        setMaxTime(Math.max(maxTime, statistics.getMaxTime()));
        setMinTime(Math.min(minTime, statistics.getMinTime()));
        setSum(sum + statistics.getSum());
        setAverageTime(getSum() / getNumberOfQueries());
    }


    @Override
    public long getAverageTime() {
        return averageTime;
    }

    void setAverageTime(long averageTime) {
        this.averageTime = averageTime;
    }

    @Override
    public long getMaxTime() {
        return maxTime;
    }

    void setMaxTime(long maxTime) {
        this.maxTime = maxTime;
    }

    @Override
    public long getMinTime() {
        return minTime;
    }

    void setMinTime(long minTime) {
        this.minTime = minTime;
    }

    @Override
    public long getNumberOfQueries() {
        return numberOfQueries;
    }

    void setNumberOfQueries(long numberOfQueries) {
        this.numberOfQueries = numberOfQueries;
    }

    long getSum() {
        return sum;
    }

    void setSum(long sum) {
        this.sum = sum;
    }
}
