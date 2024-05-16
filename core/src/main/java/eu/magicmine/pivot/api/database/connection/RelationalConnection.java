package eu.magicmine.pivot.api.database.connection;


import eu.magicmine.pivot.api.utils.connection.ConnectionData;

import java.sql.Connection;
import java.util.function.Consumer;
import java.util.logging.Logger;

public interface RelationalConnection {

    void connect(ConnectionData details, Logger logger);

    Connection getConnection();

    void asyncStatement(Consumer<Connection> consumer);

    void asyncQueue();
    void close();

}
