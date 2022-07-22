package eu.magicmine.pivot.bungee.command;

import eu.magicmine.pivot.Pivot;
import eu.magicmine.pivot.api.commands.PivotCommand;
import eu.magicmine.pivot.api.commands.annotation.Argument;
import eu.magicmine.pivot.api.commands.methods.CommandMethod;
import eu.magicmine.pivot.api.commands.methods.impl.SubCommandMethod;
import eu.magicmine.pivot.api.commands.types.ArgumentType;
import eu.magicmine.pivot.api.server.sender.PivotSender;
import lombok.Getter;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.Arrays;

public abstract class PivotBungeeCommand extends PivotCommand {

    @Getter
    private final PivotBungeeExecutor executor;

    public PivotBungeeCommand(Pivot pivot) {
        super(pivot);
        this.executor = new PivotBungeeExecutor(this);
    }

    @Override
    public void showHelp(PivotSender source, CommandMethod method) {
        CommandSender sender = (CommandSender)source.getSender();
        String name = getInfo().name() + " " + (method instanceof SubCommandMethod ? ((SubCommandMethod)method).getInfo().name() + " " : "");
        TextComponent component = new TextComponent(getInfo().color1() + "/" + name);
        for(Argument argument : method.getParameters().keySet()) {
            if(argument.type() == ArgumentType.LABEL) {
                continue;
            }
            component.addExtra(argument.choices().length != 0 ?
                    "§7" + Arrays.toString(argument.choices()).replace("\"","") + " " :
                    "§7<"+ argument.name() + ">");
        }
        String desc = method instanceof SubCommandMethod ? ((SubCommandMethod)method).getInfo().description() : getInfo().description();
        component.addExtra("§8- " + getInfo().color2() + desc);
        sender.sendMessage(component);
    }

    @Override
    public void sendArguments(PivotSender sender, String cmd) {
        for(SubCommandMethod method : getSubCommandMap().values()) {
            showHelp(sender,method);
        }
    }
}
