package eu.magicmine.pivot.spigot.handler;


import eu.magicmine.pivot.spigot.PivotSpigot;
import eu.magicmine.pivot.spigot.command.PivotSpigotCommand;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@Getter
public class CommandRegisterService {

    private final PivotSpigot api;
    private final Map<String,PivotSpigotCommand> commands;
    private final SimpleCommandMap commandMap;

    @SneakyThrows
    public CommandRegisterService(PivotSpigot api) {
        this.api = api;
        commands = new HashMap<>();
        Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
        bukkitCommandMap.setAccessible(true);
        commandMap = (SimpleCommandMap) bukkitCommandMap.get(Bukkit.getServer());
    }

    public void registerCommand(PivotSpigotCommand command) {
        try {
            commandMap.register(command.plugin(),command.getExecutor());
            commands.put(command.getInfo().name(),command);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }


    public void unregisterCommands() {
        try {
            Field commandsField = commandMap.getClass().getDeclaredField("knownCommands");
            commandsField.setAccessible(true);
            Map<String, Command> knownCommands = (Map<String, Command>) commandsField.get(commandMap);
            for(PivotSpigotCommand command : commands.values()) {
                knownCommands.remove(command.getInfo().name());
                knownCommands.remove(command.plugin() + ":"  + command.getInfo().name());
                if(command.getInfo().aliases().length > 0) {
                    for(String s : command.getInfo().aliases()) {
                        knownCommands.remove( s);
                        knownCommands.remove(command.plugin() + ":" + s);
                    }
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }


}
