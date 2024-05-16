package eu.magicmine.pivot;

import eu.magicmine.pivot.api.PivotAPI;
import eu.magicmine.pivot.api.configuration.PivotConfiguration;
import eu.magicmine.pivot.api.conversion.manager.ConversionManager;
import eu.magicmine.pivot.api.database.connection.RelationalConnection;
import eu.magicmine.pivot.api.database.connection.impl.HikariConnection;
import eu.magicmine.pivot.api.redis.IRedisConnection;
import eu.magicmine.pivot.api.redis.LettuceConnection;
import eu.magicmine.pivot.api.server.PivotServer;
import eu.magicmine.pivot.api.server.plugin.PivotPlugin;
import eu.magicmine.pivot.api.utils.connection.ConnectionData;
import lombok.Getter;

import java.util.logging.Logger;

@Getter
public class Pivot implements PivotAPI {

    private final PivotPlugin plugin;
    private PivotConfiguration configuration;
    private final PivotServer server;
    private final Logger logger;
    private ConversionManager conversionManager;
    private RelationalConnection databaseConnection;
    /*private DataSource dataSource;
    private Injector dataInjector;*/
    private IRedisConnection redisConnection;


    public Pivot(PivotPlugin plugin, PivotServer server, Logger logger) {
        this.server = server;
        this.logger = logger;
        this.plugin = plugin;
    }

    @Override
    public void onEnable() {
        conversionManager = new ConversionManager(this);
        configuration = new PivotConfiguration(plugin.getConfigurationAsMap());
        databaseConnection = new HikariConnection();
        databaseConnection.connect(getConnectionData("mysql"),logger);
        redisConnection = new LettuceConnection(this,getConnectionData("redis"));
    }


    public ConnectionData getConnectionData(String source) {
        String host = configuration.get(source + ".host",String.class);
        int port = configuration.get(source + ".port", Integer.class);
        boolean auth =  configuration.get(source + ".auth",boolean.class);
        String database = configuration.get(source + ".database",String.class);
        String username = configuration.get(source + ".username",String.class);
        String password = configuration.get(source + ".password",String.class);
        return new ConnectionData(host,port,database,auth,username,password);
    }

    @Override
    public void onDisable() {
        redisConnection.close();
        databaseConnection.close();
    }


}
