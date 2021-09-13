package eu.magicmine.pivot.api.commands;

import eu.magicmine.pivot.Pivot;
import eu.magicmine.pivot.api.commands.annotation.CommandInfo;
import eu.magicmine.pivot.api.commands.annotation.DefaultCommand;
import eu.magicmine.pivot.api.commands.annotation.SubCommand;
import eu.magicmine.pivot.api.commands.methods.impl.DefaultCommandMethod;
import eu.magicmine.pivot.api.commands.methods.impl.SubCommandMethod;
import eu.magicmine.pivot.api.server.sender.PivotPlayer;
import eu.magicmine.pivot.api.server.sender.PivotSender;
import eu.magicmine.pivot.api.utils.PivotHolder;
import lombok.Getter;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Getter
public abstract class PivotCommand extends PivotHolder {

    private DefaultCommandMethod defaultCommand;
    private CommandInfo info;
    private final Map<String, SubCommandMethod> subCommandMap;

    public PivotCommand(Pivot pivot) {
        super(pivot);
        subCommandMap = new HashMap<>();
        if(!getClass().isAnnotationPresent(CommandInfo.class)) {
            throw new IllegalStateException("CommandInfo annotation not present.");
        }
        info = getClass().getAnnotation(CommandInfo.class);
        for(Method method : getClass().getDeclaredMethods()) {
            if(defaultCommand == null && method.isAnnotationPresent(DefaultCommand.class)) {
                defaultCommand = new DefaultCommandMethod(method);
            }
            if(method.isAnnotationPresent(SubCommand.class)) {
                SubCommand info = method.getAnnotation(SubCommand.class);
                subCommandMap.put(info.name(),new SubCommandMethod(method));
            }
        }
    }

    public void onCommand(PivotSender sender,String cmd,String[] args) {
        if(!(sender instanceof PivotPlayer) && info.playersOnly()) {
            return;
        }

        

    }

    public void sendArguments() {

    }


}
