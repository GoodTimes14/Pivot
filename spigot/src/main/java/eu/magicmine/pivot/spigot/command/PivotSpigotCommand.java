package eu.magicmine.pivot.spigot.command;

import eu.magicmine.pivot.Pivot;
import eu.magicmine.pivot.api.commands.PivotCommand;
import eu.magicmine.pivot.api.commands.annotation.Argument;
import eu.magicmine.pivot.api.commands.methods.CommandMethod;
import eu.magicmine.pivot.api.commands.methods.impl.SubCommandMethod;
import eu.magicmine.pivot.api.server.sender.PivotSender;
import lombok.Getter;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

@Getter
public abstract class PivotSpigotCommand extends PivotCommand {


    private final PivotCommandExecutor executor;

    public PivotSpigotCommand(Pivot pivot) {
        super(pivot);
        executor = new PivotCommandExecutor(this);
    }

    @Override
    public void showHelp(PivotSender to, CommandMethod method) {
        CommandSender sender = (CommandSender) to.getSender();
        String name =  getInfo().color1() + "/" + getInfo().name() + " " + (method instanceof SubCommandMethod ?  ((SubCommandMethod)method).getInfo().name() + " " : "");
        for(Argument argument : method.getParameters().keySet()) {
            name += argument.choices().length  != 0 ? "§7" + Arrays.toString(argument.choices()).replace("\"","") + " " : "§7<" + argument.name() + "> ";
        }
        String desc = method instanceof SubCommandMethod ? ((SubCommandMethod)method).getInfo().description() : getInfo().description();
        name += "§8- " + getInfo().color2() + desc;
        sender.sendMessage(name);
    }

    @Override
    public void sendArguments(PivotSender sender, String cmd) {
        for(SubCommandMethod method : getSubCommandMap().values()) {
            showHelp(sender,method);
        }
    }
}
