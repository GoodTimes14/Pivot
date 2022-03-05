package eu.magicmine.pivot;

import com.google.inject.Injector;
import eu.magicmine.pivot.api.PivotAPI;
import eu.magicmine.pivot.api.conversion.manager.ConversionManager;
import eu.magicmine.pivot.api.database.DataSource;
import eu.magicmine.pivot.api.database.impl.Mongo;
import eu.magicmine.pivot.api.database.provider.DataProvider;
import eu.magicmine.pivot.api.redis.RedisManager;
import eu.magicmine.pivot.api.server.PivotServer;
import eu.magicmine.pivot.api.server.plugin.PivotPlugin;
import eu.magicmine.pivot.api.utils.connection.ConnectionData;
import lombok.Getter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Logger;

@Getter
public class Pivot implements PivotAPI {

    private PivotPlugin plugin;
    private final PivotServer server;
    private final Logger logger;
    private final File configurationFile;
    private ConversionManager conversionManager;
    private DataSource dataSource;
    private Injector dataInjector;
    private RedisManager redisManager;


    public Pivot(PivotPlugin plugin, PivotServer server, Logger logger, File configurationFile) {
        this.server = server;
        this.logger = logger;
        this.configurationFile = configurationFile;
        this.plugin = plugin;
    }

    @Override
    public void onEnable() {
        conversionManager = new ConversionManager(this);
        dataSource = new Mongo();
        dataInjector = dataSource.openConnection(new ConnectionData("localhost",27017,"admin",false,"",""));
        redisManager = new RedisManager(this,new ConnectionData("localhost",6379,"",false,"",""));
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

    @Override
    public void onDisable() {
        dataSource.close();
    }


}
