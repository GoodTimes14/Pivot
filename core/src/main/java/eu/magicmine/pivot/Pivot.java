package eu.magicmine.pivot;

import com.google.inject.Injector;
import eu.magicmine.pivot.api.PivotAPI;
import eu.magicmine.pivot.api.configuration.PivotConfiguration;
import eu.magicmine.pivot.api.conversion.manager.ConversionManager;
import eu.magicmine.pivot.api.database.DataSource;
import eu.magicmine.pivot.api.database.impl.Mongo;
import eu.magicmine.pivot.api.database.provider.DataProvider;
import eu.magicmine.pivot.api.redis.RedisManager;
import eu.magicmine.pivot.api.server.PivotServer;
import eu.magicmine.pivot.api.server.plugin.PivotPlugin;
import eu.magicmine.pivot.api.utils.connection.ConnectionData;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Logger;

@Getter
public class Pivot implements PivotAPI {

    private PivotPlugin plugin;
    private PivotConfiguration configuration;
    private final PivotServer server;
    private final Logger logger;
    private ConversionManager conversionManager;
    private DataSource dataSource;
    private Injector dataInjector;
    private RedisManager redisManager;


    public Pivot(PivotPlugin plugin, PivotServer server, Logger logger) {
        this.server = server;
        this.logger = logger;
        this.plugin = plugin;
    }

    @Override
    public void onEnable() {
        conversionManager = new ConversionManager(this);
        configuration = new PivotConfiguration(plugin.getConfigurationAsMap());
        dataSource = new Mongo();
        ConnectionData data =
                getConnectionData("mongodb");
        System.out.println(data);
        dataInjector = dataSource.openConnection(getConnectionData("mongodb"));
        System.out.println("connesso");
        redisManager = new RedisManager(this,getConnectionData("redis"));
    }

    public <T extends DataProvider> T registerDataProvider(String pluginName, Class<T> dataProvider) {
        T provider = dataInjector.getInstance(dataProvider);
        if(dataSource.getLoadedProviders().containsKey(pluginName)) {
            dataSource.getLoadedProviders().get(pluginName).add(provider);
        } else {
            dataSource.getLoadedProviders().put(pluginName,new ArrayList<>(Collections.singletonList(provider)));
        }
        return provider;
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
        dataSource.close();
    }


}
