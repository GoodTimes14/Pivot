package eu.magicmine.pivot.spigot;

import eu.magicmine.pivot.Pivot;
import eu.magicmine.pivot.api.commands.PivotCommand;
import eu.magicmine.pivot.api.server.plugin.PivotPlugin;
import eu.magicmine.pivot.spigot.command.PivotSpigotCommand;
import eu.magicmine.pivot.spigot.handler.CommandRegisterService;
import eu.magicmine.pivot.spigot.server.PivotSpigotServer;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;

@Getter
public class PivotSpigot extends JavaPlugin implements PivotPlugin {


    private CommandRegisterService registerService;
    private Pivot pivot;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        registerService = new CommandRegisterService(this);
        pivot = new Pivot(this,new PivotSpigotServer(this), Bukkit.getLogger());
        pivot.onEnable();
    }

    @Override
    public void onDisable() {
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
        registerService.registerCommand((PivotSpigotCommand) command);
    }

    @Override
    public Map<String, Object> getConfigurationAsMap() {
        try {
            return (Map<String, Object>) new Yaml().load(new FileInputStream(new File(getDataFolder(),"config.yml")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
