package eu.magicmine.pivot.velocity;


import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import eu.magicmine.pivot.Pivot;
import eu.magicmine.pivot.api.commands.PivotCommand;
import eu.magicmine.pivot.api.server.plugin.PivotPlugin;
import eu.magicmine.pivot.api.utils.classes.ClassUtils;
import eu.magicmine.pivot.velocity.command.PivotVelocityCommand;
import eu.magicmine.pivot.velocity.server.PivotVelocityServer;
import lombok.Getter;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;


@Plugin(id = "pivot", name = "Pivot", version = "1.0.0",
        description = "Command framework", authors = {"MagicMine"})
@Getter
public class PivotVelocity implements PivotPlugin {

    private final ProxyServer server;
    private final Logger logger;
    private final File dataDirectory;
    private Pivot pivot;
    private final Set<String> releasedProviders = new HashSet<>();

    @Inject
    public PivotVelocity(ProxyServer server, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = Logger.getLogger("Pivot");
        this.dataDirectory = dataDirectory.toFile();
        if(!this.dataDirectory.exists()) {
            this.dataDirectory.mkdir();
        }
    }

    @Subscribe
    public void onEnable(ProxyInitializeEvent event) {
        pivot = new Pivot(this,new PivotVelocityServer(server),logger);
        pivot.onEnable();
    }

    @Subscribe
    public void onDisable(ProxyShutdownEvent event) {
        /*if(pivot.getDataSource().getLoadedProviders().isEmpty()) {
            logger.log(Level.INFO,"All clear, disabling Pivot");
            pivot.onDisable();
        } else {
            logger.log(Level.INFO,"Looks like the datasource is still in use, waiting for data providers to release");
        }*/
    }

    /*public void releaseProvider(String plugin) {
        if(pivot.getDataSource().getLoadedProviders().containsKey(plugin)) {
            releasedProviders.add(plugin);
            if(releasedProviders.size() == pivot.getDataSource().getLoadedProviders().size()) {
                logger.log(Level.INFO,"All clear, disabling Pivot");
                pivot.onDisable();
            }
        }
    }*/

    @Override
    public void registerCommands(PivotCommand... commands) {
        for(PivotCommand command : commands) {
            registerCommand(command);
        }
    }

    @Override
    public void registerCommand(PivotCommand command) {
        server.getCommandManager().register(command.getInfo().name(),(PivotVelocityCommand) command,command.getInfo().aliases());
    }

    @Override
    public Map<String, Object> getConfigurationAsMap() {
        File file = ClassUtils.getFileOrDefault(getClass().getClassLoader(),"config.yml",dataDirectory);
        try {
            return new Yaml().load(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new IllegalStateException("Error loading the configuration file");
        }
    }
}
