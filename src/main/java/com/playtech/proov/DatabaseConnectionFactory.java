package com.playtech.proov;


import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.*;
import java.util.Properties;

/**
 * Provides database connection pooling
 */
public class DatabaseConnectionFactory {

    private static final Logger LOG = LoggerFactory.getLogger(DatabaseConnectionFactory.class);
    private static final DatabaseConnectionFactory factory = new DatabaseConnectionFactory();
    private PoolingDataSource dataSource;

    private DatabaseConnectionFactory() {
        init();
    }

    public static DatabaseConnectionFactory getFactory() {
        return factory;
    }

    public java.sql.Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    void init() {

        URL resource = Thread.currentThread().getContextClassLoader().getResource("database.properties");

        try (InputStream inputStream = resource.openStream()) {
            Properties properties = new Properties();
            properties.load(inputStream);

            String driver = properties.getProperty("db.driver");
            String dbUrl = properties.getProperty("db.url");
            String dbName = properties.getProperty("db.name");
            String userName = properties.getProperty("db.username");
            String password = properties.getProperty("db.password");
            String connectionUrl = dbUrl + "/" + dbName;

            LOG.info("Database url is {}", connectionUrl);

            Class.forName(driver).newInstance();

            GenericObjectPool connectionPool = new GenericObjectPool();
            connectionPool.setMaxActive(30);

            ConnectionFactory cf = new DriverManagerConnectionFactory(dbUrl, userName, password);

            new PoolableConnectionFactory(cf, connectionPool, null, null, false, true);

            dataSource = new PoolingDataSource(connectionPool);


        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }
}
