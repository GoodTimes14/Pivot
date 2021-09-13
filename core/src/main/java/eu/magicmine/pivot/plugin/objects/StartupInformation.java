package eu.magicmine.pivot.plugin.objects;

import lombok.Data;

import java.io.File;
import java.util.logging.Logger;

@Data
public class StartupInformation {

    private final Logger logger;
    private final File configuration;


}
