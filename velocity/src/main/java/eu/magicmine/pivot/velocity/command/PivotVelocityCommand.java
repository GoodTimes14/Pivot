package eu.magicmine.pivot.velocity.command;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import eu.magicmine.pivot.Pivot;
import eu.magicmine.pivot.api.commands.PivotCommand;
import eu.magicmine.pivot.api.commands.annotation.Argument;
import eu.magicmine.pivot.api.commands.methods.CommandMethod;
import eu.magicmine.pivot.api.commands.methods.impl.SubCommandMethod;
import eu.magicmine.pivot.api.commands.types.ArgumentType;
import eu.magicmine.pivot.api.server.sender.PivotPlayer;
import eu.magicmine.pivot.api.server.sender.PivotSender;
import net.kyori.adventure.text.Component;

import java.util.Arrays;

public abstract class PivotVelocityCommand extends PivotCommand implements SimpleCommand {

    public PivotVelocityCommand(Pivot pivot) {
        super(pivot);
    }

    @Override
    public void showHelp(PivotSender sender, CommandMethod method) {
        CommandSource source = (CommandSource) sender.getSender();
        String name = getInfo().name() + " " + (method instanceof SubCommandMethod ?  ((SubCommandMethod)method).getInfo().name() + " " : "");
        Component component = Component.text(getInfo().color1() + "/" + name);
        for(Argument argument : method.getParameters().keySet()) {
            if(argument.type() == ArgumentType.LABEL) {
                continue;
            }
            component = component.append(argument.choices().length  != 0 ?
                    Component.text("§7" + Arrays.toString(argument.choices()).replace("\"","") + " ") :
                    Component.text("§7<" + argument.name() + "> "));
        }
        String desc = method instanceof SubCommandMethod ? ((SubCommandMethod)method).getInfo().description() : getInfo().description();
        component = component.append(Component.text("§8- " + getInfo().color2() + desc));
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
            invocation.source().sendMessage(Component.text("Comando solo players bruh."));
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
