package eu.magicmine.pivot.api.utils;

import lombok.Data;

@Data
public class ConnectionData {

    private final String host;
    private final int port;
    private final String database;
    private final boolean auth;
    private final String username;
    private final String password;

}