package eu.magicmine.pivot.api.database.connection.impl;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import eu.magicmine.pivot.api.database.connection.RelationalConnection;
import eu.magicmine.pivot.api.database.connection.RelationalInteraction;
import eu.magicmine.pivot.api.utils.connection.ConnectionData;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HikariConnection implements RelationalConnection {

    private HikariDataSource dataSource;
    private Logger logger;
    private BlockingQueue<Consumer<Connection>> taskQueue = new LinkedBlockingQueue<>();

    private Thread asyncThread;

    @Override
    public void connect(ConnectionData details, Logger logger) {
        this.logger = logger;
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + details.getHost() + ":" + details.getPort() + "/" + details.getDatabase() + "?autoReconnect=true&allowMultiQueries=true");
        config.setUsername(details.getUsername());
        if(details.isAuth()) {
            config.setPassword(details.getPassword());
        }

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("useLocalSessionState", "true");
        config.addDataSourceProperty("rewriteBatchedStatements", "true");
        config.addDataSourceProperty("cacheResultSetMetadata", "true");
        config.addDataSourceProperty("cacheServerConfiguration", "true");
        config.addDataSourceProperty("elideSetAutoCommits", "true");
        config.addDataSourceProperty("maintainTimeStats", "false");

        dataSource = new HikariDataSource(config);

        asyncThread = new Thread(this::asyncQueue);
        asyncThread.start();

        logger.log(Level.FINE, "Connection to SQL Database established successfully");
    }

    @Override
    public Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {

            logger.log(Level.SEVERE,"Error while creating connection: ", e);
            return null;
        }
    }

    @Override
    public RelationalInteraction createInteraction(boolean autoCommit) {

        try {

            SQLInteraction interaction = new SQLInteraction(getConnection());
            interaction.autoCommit(autoCommit);

            return interaction;

        } catch (SQLException e) {

            logger.log(Level.SEVERE,"Error while creating interaction: ", e);
            return null;
        }

    }

    @Override
    public void asyncQueue() {
        while (!Thread.currentThread().isInterrupted()) {

            try {

                Consumer<Connection> consumer = taskQueue.take();
                executeAsyncStatement(consumer);

            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();

            }
        }
    }

    private void executeAsyncStatement(Consumer<Connection> consumer) {
        try(Connection connection = dataSource.getConnection()) {

            consumer.accept(connection);

        } catch (SQLException ex) {

            logger.log(Level.SEVERE,"Error while executing an async statement",ex);

        }
    }

    @Override
    public void asyncStatement(Consumer<Connection> consumer) {
        try {
            taskQueue.put(consumer);
        } catch (InterruptedException e) {
            //Ignoriamo
        }
    }

    @Override
    public void close() {
        asyncThread.interrupt();
        while (!taskQueue.isEmpty()) {
            executeAsyncStatement(taskQueue.poll());
        }
        dataSource.close();
    }

}
