package com.playtech.proov.services.wallet.model;

import com.playtech.proov.services.ServiceResponse;

import java.io.Serializable;
import java.math.BigDecimal;


public class IncrementBalanceResponse extends ServiceResponse implements Serializable {

    private static final long serialVersionUID = 1;

    private BigDecimal balance;
    private BigDecimal increment;
    private int balanceVersion;

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public int getBalanceVersion() {
        return balanceVersion;
    }

    public void setBalanceVersion(int balanceVersion) {
        this.balanceVersion = balanceVersion;
    }

    public BigDecimal getIncrement() {
        return increment;
    }

    public void setIncrement(BigDecimal increment) {
        this.increment = increment;
    }


    @Override
    public String toString() {
        return "IncrementBalanceResponse{" +
                "balance=" + balance +
                ", increment=" + increment +
                ", balanceVersion=" + balanceVersion +
                '}';
    }
}
