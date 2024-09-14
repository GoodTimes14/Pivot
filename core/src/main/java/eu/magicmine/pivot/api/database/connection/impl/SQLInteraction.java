package eu.magicmine.pivot.api.database.connection.impl;

import eu.magicmine.pivot.api.database.connection.RelationalInteraction;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

@RequiredArgsConstructor
public class SQLInteraction implements RelationalInteraction {

    private final Connection connection;
    private final Logger logger;

    @Override
    public ResultSet query(String sql, Object... parameters) throws SQLException {
        try(PreparedStatement statement = createStatement(sql,parameters)) {
            return statement.executeQuery();
        }
    }

    @Override
    public int update(String sql, Object... parameters) throws SQLException {
        try(PreparedStatement statement = createStatement(sql,parameters)) {
            return statement.executeUpdate();
        }
    }

    @Override
    public void commit() throws SQLException {
        connection.commit();
    }

    @Override
    public boolean safeCommit() {

        try {

            connection.commit();

            return true;
        } catch (SQLException e) {

            logger.log(Level.SEVERE,"Safe commit returned error: ",e);

            logger.log(Level.INFO,"trying rollback...");

            try {
                connection.rollback();

            } catch (Exception exception) {

                logger.log(Level.SEVERE,"Rollback failed",exception);

            }

            return false;
        }

    }

    @Override
    public void rollback() throws SQLException {
        connection.rollback();
    }

    @Override
    public boolean isAutoCommit()  throws SQLException {
        return connection.getAutoCommit();
    }

    @Override
    public void autoCommit(boolean autoCommit) throws SQLException {
        connection.setAutoCommit(autoCommit);
    }

    @Override
    public void close() throws SQLException {
        connection.close();
    }

    private PreparedStatement createStatement(String sql, Object... parameters) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            for (int i = 0; i < parameters.length; i++) {
                Object param = parameters[i];
                statement.setObject(i,param);

            }
            return statement;
        }
    }

}
