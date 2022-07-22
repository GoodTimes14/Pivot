package eu.magicmine.pivot.bungee.command;

import eu.magicmine.pivot.api.server.sender.PivotPlayer;
import eu.magicmine.pivot.api.server.sender.PivotSender;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class PivotBungeeExecutor extends Command {

    private final PivotBungeeCommand command;

    public PivotBungeeExecutor(PivotBungeeCommand command) {
        super(command.getInfo().name(),"",command.getInfo().aliases());
        this.command = command;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof ProxiedPlayer) && command.getInfo().playersOnly()) {
            sender.sendMessage(new TextComponent("Questo comando è eseguibile solo in-game."));
            return;
        }
        if(command.getInfo().permission().length() != 0) {
            if(!sender.hasPermission(command.getInfo().permission())) {
                sender.sendMessage(new TextComponent(command.noPermsMessage()));
                return;
            }
        }
        command.onCommand(sender instanceof ProxiedPlayer ? new PivotPlayer(sender) : new PivotSender(sender),command.getInfo().name(),args);
    }
}
