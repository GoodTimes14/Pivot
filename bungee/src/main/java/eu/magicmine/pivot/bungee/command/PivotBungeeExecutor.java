package eu.magicmine.pivot.bungee.command;

import eu.magicmine.pivot.api.server.sender.PivotPlayer;
import eu.magicmine.pivot.api.server.sender.PivotSender;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

public class PivotBungeeExecutor extends Command implements TabExecutor {

    private final PivotBungeeCommand command;

    public PivotBungeeExecutor(PivotBungeeCommand command) {
        super(command.getInfo().name(), command.getInfo().permission(), command.getInfo().aliases());
        this.command = command;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof ProxiedPlayer) && command.getInfo().playersOnly()) {
            sender.sendMessage(new TextComponent("Questo comando è eseguibile solo in-game."));
            return;
        }

        if(!command.getInfo().permission().isEmpty() && !sender.hasPermission(command.getInfo().permission())) {
            sender.sendMessage(new TextComponent(command.noPermsMessage()));
            return;
        }

        command.onCommand(sender instanceof ProxiedPlayer ? new PivotPlayer(sender) : new PivotSender(sender),command.getInfo().name(),args);
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        return command.onTabComplete(new PivotSender(sender), args);
    }
}
