package com.playtech.proov.services.wallet.impl;


import com.playtech.proov.DatabaseConnectionFactory;
import com.playtech.proov.services.wallet.WalletService;
import com.playtech.proov.services.wallet.model.IncrementBalanceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


@Resource
public class WalletServiceImpl implements WalletService {

    private static final Logger LOG = LoggerFactory.getLogger(WalletServiceImpl.class);
    public static final int ERROR_CODE = 1;

    @Override
    public IncrementBalanceResponse incrementBalance(String userName, BigDecimal increment) {

        LOG.info("Update balance for user {}", userName);

        IncrementBalanceResponse response = new IncrementBalanceResponse();

        String selectSql = "SELECT balance_version, balance FROM PLAYER  WHERE balance_version = " +
                "(SELECT MAX(BALANCE_VERSION) FROM PLAYER WHERE USERNAME = ?)";

        String insertSql = "INSERT INTO PLAYER VALUES(?,?,?)";

        PreparedStatement queryStatement = null;
        PreparedStatement insertStatement = null;
        Connection connection = null;
        try {
            connection = DatabaseConnectionFactory.getFactory().getConnection();
            connection.setAutoCommit(false);

            queryStatement = connection.prepareStatement(selectSql);
            queryStatement.setString(1, userName);
            ResultSet resultSet = queryStatement.executeQuery();
            int currentVersion = 0;
            BigDecimal currentBalance = BigDecimal.ZERO;
            if (resultSet.next()) {
                currentVersion = resultSet.getInt(1);
                currentBalance = resultSet.getBigDecimal(2);
            }

            LOG.debug("Current balance version is {}", currentVersion);
            LOG.debug("Current balance is {}", currentBalance);

            int newVersion = ++currentVersion;
            BigDecimal newBalance = currentBalance.add(increment);

            LOG.debug("New balance version is {}", newVersion);
            LOG.debug("New balance is {}", newBalance);

            insertStatement = connection.prepareStatement(insertSql);
            insertStatement.setString(1, userName);
            insertStatement.setInt(2, newVersion);
            insertStatement.setBigDecimal(3, newBalance);

            insertStatement.executeUpdate();
            connection.commit();

            response.setBalance(newBalance);
            response.setBalanceVersion(newVersion);

        } catch (SQLException e) {
            LOG.error(e.getMessage(), e);
            response.setErrorCode(ERROR_CODE);
        } finally {
            closeStatement(queryStatement);
            closeStatement(insertStatement);
            try {
                if (connection != null) {
                    connection.setAutoCommit(true);
                }
            } catch (SQLException e) {
                LOG.error(e.getMessage(), e);
            }
        }

        return response;
    }

    private void closeStatement(PreparedStatement queryStatement) {
        if (queryStatement != null) {
            try {
                queryStatement.close();
            } catch (SQLException e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }

    @Override
    public BigDecimal getBalance() {
        return BigDecimal.ONE;
    }

    public static void main(String[] args) throws SQLException {
        WalletServiceImpl service = new WalletServiceImpl();
        service.incrementBalance("andrus", BigDecimal.valueOf(100));
    }

}


