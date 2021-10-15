package eu.magicmine.pivot;

import com.google.inject.Injector;
import eu.magicmine.pivot.api.PivotAPI;
import eu.magicmine.pivot.api.conversion.manager.ConversionManager;
import eu.magicmine.pivot.api.database.DataSource;
import eu.magicmine.pivot.api.database.impl.Mongo;
import eu.magicmine.pivot.api.database.loader.DataLoader;
import eu.magicmine.pivot.api.server.PivotServer;
import eu.magicmine.pivot.api.server.plugin.PivotPlugin;
import eu.magicmine.pivot.api.utils.ConnectionData;
import lombok.Getter;

import java.io.File;
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
    }

    public <T extends DataLoader> T initLoader(Class<T> dataLoader) {
        return dataInjector.getInstance(dataLoader);
    }

    @Override
    public void onDisable() {

    }


}
