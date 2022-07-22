package eu.magicmine.pivot.bungee;

import com.google.inject.Inject;
import eu.magicmine.pivot.Pivot;
import eu.magicmine.pivot.api.commands.PivotCommand;
import eu.magicmine.pivot.api.server.plugin.PivotPlugin;
import eu.magicmine.pivot.bungee.command.PivotBungeeCommand;
import eu.magicmine.pivot.bungee.handler.CommandRegisterService;
import eu.magicmine.pivot.bungee.server.PivotBungeeServer;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

@Getter
public class PivotBungee extends Plugin implements PivotPlugin {

    private final ProxyServer server;
    private final Logger logger;
    private final File dataDirectory;
    private CommandRegisterService registerService;
    private Pivot pivot;
    private final Set<String> releasedProviders = new HashSet<>();

    @Inject
    public PivotBungee(ProxyServer server, Path dataDirectory) {
        this.server = server;
        this.logger = new LoggerPrefix("PivotBungee");
        this.dataDirectory = dataDirectory.toFile();
        if(!this.dataDirectory.exists()) {
            this.dataDirectory.mkdir();
        }
    }

    @Override
    public void onEnable() {
        registerService = new CommandRegisterService(this);
        pivot = new Pivot(this,new PivotBungeeServer(server),logger);
        pivot.onEnable();
    }

    @Override
    public void onDisable() {
        if(pivot.getDataSource().getLoadedProviders().isEmpty()) {
            logger.log(Level.INFO,"All clear, disabling Pivot");
            pivot.onDisable();
        } else {
            logger.log(Level.INFO,"Looks like the datasource is still in use, waiting for data providers to release");
        }
    }

    public void releaseProvider(String plugin) {
        if(pivot.getDataSource().getLoadedProviders().containsKey(plugin)) {
            releasedProviders.add(plugin);
            if(releasedProviders.size() == pivot.getDataSource().getLoadedProviders().size()) {
                logger.log(Level.INFO,"All clear, disabling Pivot");
                pivot.onDisable();
            }
        }
    }

    @Override
    public void registerCommands(PivotCommand... commands) {
        for(PivotCommand command : commands) {
            registerCommand(command);
        }
    }

    @Override
    public void registerCommand(PivotCommand command) {
        registerService.registerCommand((PivotBungeeCommand) command);
    }

    @Override
    public Map<String, Object> getConfigurationAsMap() {
        return null;
    }

    private static class LoggerPrefix extends Logger {

        private final String prefix;

        public LoggerPrefix(String prefix) {

            super(prefix, null);

            this.prefix = "[" + prefix + "] ";
            setParent(ProxyServer.getInstance().getLogger());

        }

        @Override
        public void log(LogRecord logRecord) {
            logRecord.setMessage(this.prefix + logRecord.getMessage());
            super.log(logRecord);
        }

    }
}
