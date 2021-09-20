package eu.magicmine.pivot;

import eu.magicmine.pivot.api.PivotAPI;
import eu.magicmine.pivot.api.conversion.manager.ConversionManager;
import eu.magicmine.pivot.api.server.PivotServer;
import eu.magicmine.pivot.api.server.plugin.PivotPlugin;
import lombok.Getter;

import java.io.File;
import java.util.logging.Logger;

@Getter
public class Pivot implements PivotAPI {

    private static Pivot instance;
    private PivotPlugin plugin;
    private final PivotServer server;
    private final Logger logger;
    private final File configurationFile;
    private ConversionManager conversionManager;

    public Pivot(PivotPlugin plugin, PivotServer server, Logger logger, File configurationFile) {
        this.server = server;
        this.logger = logger;
        this.configurationFile = configurationFile;
        this.plugin = plugin;
        Pivot.instance = this;
    }

    public static Pivot get() {
        return Pivot.instance;
    }

    @Override
    public void onEnable() {
        conversionManager = new ConversionManager(this);
    }

    @Override
    public void onDisable() {

    }


}
