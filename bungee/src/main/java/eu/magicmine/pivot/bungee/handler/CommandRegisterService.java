package eu.magicmine.pivot.bungee.handler;

import eu.magicmine.pivot.api.commands.PivotCommand;
import eu.magicmine.pivot.bungee.PivotBungee;
import eu.magicmine.pivot.bungee.command.PivotBungeeCommand;

import java.util.HashMap;
import java.util.Map;

public class CommandRegisterService {

    private final PivotBungee api;
    private Map<String, PivotCommand> commands;

    public CommandRegisterService(PivotBungee api) {
        this.api = api;
        commands = new HashMap<>();
    }


    public void registerCommand(PivotBungeeCommand command) {
        commands.put(command.getInfo().name(),command);
        api.getProxy().getPluginManager().registerCommand(api,command.getExecutor());
    }

}
