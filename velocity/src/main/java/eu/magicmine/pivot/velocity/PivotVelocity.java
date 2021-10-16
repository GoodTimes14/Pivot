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
import eu.magicmine.pivot.velocity.command.PivotVelocityCommand;
import eu.magicmine.pivot.velocity.server.PivotVelocityServer;
import lombok.Getter;

import java.io.File;
import java.nio.file.Path;
import java.util.logging.Logger;


@Plugin(id = "pivot", name = "Pivot", version = "1.0.0",
        description = "Command framework", authors = {"MagicMine"})
@Getter
public class PivotVelocity implements PivotPlugin {

    private final ProxyServer server;
    private final Logger logger;
    private final File dataDirectory;
    private Pivot pivot;

    @Inject
    public PivotVelocity(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory.toFile();
        if(!this.dataDirectory.exists()) {
            this.dataDirectory.mkdir();
        }
    }

    @Subscribe
    public void onEnable(ProxyInitializeEvent event) {
        pivot = new Pivot(this,new PivotVelocityServer(server),logger,dataDirectory);
        pivot.onEnable();

    }

    @Subscribe
    public void onDisable(ProxyShutdownEvent event) {
        pivot.onDisable();
    }

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
}
