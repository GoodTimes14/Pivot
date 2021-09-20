package eu.magicmine.pivot.spigot;

import eu.magicmine.pivot.Pivot;
import eu.magicmine.pivot.api.commands.PivotCommand;
import eu.magicmine.pivot.api.server.plugin.PivotPlugin;
import eu.magicmine.pivot.spigot.command.PivotSpigotCommand;
import eu.magicmine.pivot.spigot.handler.CommandRegisterService;
import eu.magicmine.pivot.spigot.server.PivotSpigotServer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class PivotSpigot extends JavaPlugin implements PivotPlugin {


    private CommandRegisterService registerService;
    private Pivot pivot;

    @Override
    public void onEnable() {
        registerService = new CommandRegisterService(this);
        pivot = new Pivot(this,new PivotSpigotServer(this), Bukkit.getLogger(),/* Non lo sto usando ora*/null);
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
}
