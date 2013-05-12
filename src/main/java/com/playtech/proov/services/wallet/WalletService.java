package com.playtech.proov.services.wallet;


import com.playtech.proov.services.wallet.model.IncrementBalanceResponse;

import java.math.BigDecimal;

/**
 * Updates balance data in database
 */
public interface WalletService {
    BigDecimal getBalance();

    IncrementBalanceResponse incrementBalance(String userName, BigDecimal balance);
}
