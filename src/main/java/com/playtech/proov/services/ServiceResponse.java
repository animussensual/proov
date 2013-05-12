package com.playtech.proov.services;


/**
 * General service response
 */
public class ServiceResponse {
    private int errorCode;
    private long transactionId;

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(long transactionId) {
        this.transactionId = transactionId;
    }
}
