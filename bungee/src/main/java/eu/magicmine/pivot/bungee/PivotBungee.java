package eu.magicmine.pivot.bungee;

import eu.magicmine.pivot.Pivot;
import eu.magicmine.pivot.api.commands.PivotCommand;
import eu.magicmine.pivot.api.server.plugin.PivotPlugin;
import eu.magicmine.pivot.bungee.command.PivotBungeeCommand;
import eu.magicmine.pivot.bungee.handler.CommandRegisterService;
import eu.magicmine.pivot.bungee.server.PivotBungeeServer;
import eu.magicmine.pivot.bungee.util.ConfigUtil;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

@Getter
public class PivotBungee extends Plugin implements PivotPlugin {


    private Logger logger;
    private CommandRegisterService registerService;
    private Pivot pivot;
    private final Set<String> releasedProviders = new HashSet<>();

    @Override
    public void onEnable() {
        ConfigUtil.saveDefaultConfig(getDataFolder(),"config.yml",getClass().getClassLoader());
        registerService = new CommandRegisterService(this);
        logger = new LoggerPrefix("Pivot");
        pivot = new Pivot(this,new PivotBungeeServer(getProxy()),logger);
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
        try {
            return new Yaml().load(new FileInputStream(new File(getDataFolder(),"config.yml")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }

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
