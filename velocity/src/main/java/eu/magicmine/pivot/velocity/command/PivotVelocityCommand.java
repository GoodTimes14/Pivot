package eu.magicmine.pivot.velocity.command;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import eu.magicmine.pivot.Pivot;
import eu.magicmine.pivot.api.commands.PivotCommand;
import eu.magicmine.pivot.api.commands.annotation.Argument;
import eu.magicmine.pivot.api.commands.methods.impl.SubCommandMethod;
import eu.magicmine.pivot.api.server.sender.PivotPlayer;
import eu.magicmine.pivot.api.server.sender.PivotSender;
import net.kyori.adventure.text.Component;

import java.util.Arrays;

public abstract class PivotVelocityCommand extends PivotCommand implements SimpleCommand {

    public PivotVelocityCommand(Pivot pivot) {

        super(pivot);
    }

    @Override
    public void showHelp(PivotSender sender, SubCommandMethod subCommandMethod) {
        CommandSource source = (CommandSource) sender.getSender();
        Component component = Component.text("§f/§7" + getInfo().name() + " " + subCommandMethod.getInfo().name() + " ");
        for(Argument argument : subCommandMethod.getParameters().keySet()) {
            component = component.append(argument.choices().length  != 0 ?
                    Component.text("§7" + Arrays.toString(argument.choices())) :
                    Component.text("§7<" + argument.name() + "> "));
        }
        component = component.append(Component.text("§8- " + subCommandMethod.getInfo().description()));
        source.sendMessage(component);
    }

    @Override
    public void sendArguments(PivotSender sender, String cmd) {
        for(SubCommandMethod method : getSubCommandMap().values()) {
            showHelp(sender,method);
        }
    }

    @Override
    public void execute(Invocation invocation) {
        if(!(invocation.source() instanceof Player) && getInfo().playersOnly()) {
            invocation.source().sendMessage(Component.text("Only players bro."));
            return;
        }
        if(getInfo().permission().length() != 0) {
            if(!invocation.source().hasPermission(getInfo().permission())) {
                invocation.source().sendMessage(Component.text(noPermsMessage()));
                return;
            }
        }
        onCommand(invocation.source() instanceof Player ?
                new PivotPlayer(invocation.source()) : new PivotSender(invocation.source()),invocation.alias(),
                invocation.arguments());
    }
}
