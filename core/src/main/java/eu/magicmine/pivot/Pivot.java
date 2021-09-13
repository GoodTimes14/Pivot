package eu.magicmine.pivot;

import eu.magicmine.pivot.api.PivotAPI;
import eu.magicmine.pivot.api.conversion.manager.ConversionManager;
import eu.magicmine.pivot.api.server.PivotServer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.util.logging.Logger;

@RequiredArgsConstructor
@Getter
public class Pivot implements PivotAPI {

    private final PivotServer server;
    private final Logger logger;
    private final File configurationFile;
    private ConversionManager conversionManager;

    @Override
    public void onEnable() {
        conversionManager = new ConversionManager(this);
    }

    @Override
    public void onDisable() {

    }


}
