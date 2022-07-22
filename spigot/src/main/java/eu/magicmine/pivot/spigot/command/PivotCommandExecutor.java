package eu.magicmine.pivot.spigot.command;

import eu.magicmine.pivot.api.server.sender.PivotSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;

public class PivotCommandExecutor extends BukkitCommand {

    private final PivotSpigotCommand command;

    public PivotCommandExecutor(PivotSpigotCommand command) {
        super("","","",new ArrayList<>());
        this.command = command;
        setName(command.getInfo().name());
        setLabel(command.getInfo().name());
        setDescription(command.getInfo().description());
        setAliases(Arrays.asList(command.getInfo().aliases()));
    }

    @Override
    public boolean execute(CommandSender sender, String cmd,String[] args) {
        if(!(sender instanceof Player) && command.getInfo().playersOnly()) {
            sender.sendMessage("Questo comando è eseguibile solo in-game.");
            return false;
        }
        if(command.getInfo().permission().length() != 0) {
            if(!sender.hasPermission(command.getInfo().permission())) {
                sender.sendMessage(command.noPermsMessage());
                return false;
            }
        }
        command.onCommand(new PivotSender(sender),cmd, args);
        return true;
    }
}
