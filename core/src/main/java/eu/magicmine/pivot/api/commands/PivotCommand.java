package eu.magicmine.pivot.api.commands;

import eu.magicmine.pivot.Pivot;
import eu.magicmine.pivot.api.commands.annotation.CommandInfo;
import eu.magicmine.pivot.api.commands.annotation.DefaultCommand;
import eu.magicmine.pivot.api.commands.annotation.SubCommand;
import eu.magicmine.pivot.api.commands.methods.impl.DefaultCommandMethod;
import eu.magicmine.pivot.api.commands.methods.impl.SubCommandMethod;
import eu.magicmine.pivot.api.conversion.Converter;
import eu.magicmine.pivot.api.server.sender.PivotPlayer;
import eu.magicmine.pivot.api.server.sender.PivotSender;
import eu.magicmine.pivot.api.utils.PivotHolder;
import lombok.Getter;
import lombok.SneakyThrows;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
                defaultCommand = new DefaultCommandMethod(this,method);
            }
            if(method.isAnnotationPresent(SubCommand.class)) {
                SubCommand info = method.getAnnotation(SubCommand.class);
                subCommandMap.put(info.name(),new SubCommandMethod(this,method));
            }
        }
    }

    @SneakyThrows
    public void onCommand(PivotSender sender, String cmd, String[] args) {
        if(!(sender instanceof PivotPlayer) && info.playersOnly()) {
            return;
        }
        if(args.length == 0 && defaultCommand == null) {
            sendArguments(sender,cmd);
            return;
        } else if(args.length == 0) {
            defaultCommand.getMethod().invoke(defaultCommand.getHolder(),defaultCommand.getSenderClass().cast(sender.getSender()));
        }
        SubCommandMethod subCommandMethod = subCommandMap.get(args[0]);
        if(subCommandMethod == null) {
            sendArguments(sender,cmd);
            return;
        }
        Object[] params = new Object[subCommandMethod.getParameters().size() + 1];
        params[0] = defaultCommand.getSenderClass().cast(sender.getSender());
        boolean valid = true;
        for(int i = 1;i < params.length;i++) {
            Class<?> type = subCommandMethod.getParameters().get(i).getType();
            Optional<Converter<?>> optionalConverter = pivot.getConversionManager().getConverter(type);
            if(optionalConverter.isPresent()) {
                if(type.isAssignableFrom(String.class)) {
                    params[i] = args[i];
                    continue;
                }
                Converter<?> converter = optionalConverter.get();
                if(!converter.canConvert(args[i])) {
                    valid = false;
                    showHelp(subCommandMethod);
                    break;
                }
                params[i] = converter.convert(args[i]);
            }
        }
        if(valid) {
            subCommandMethod.getMethod().invoke(subCommandMethod.getHolder(),params);
        }
    }

    public abstract void showHelp(SubCommandMethod subCommandMethod);

    public abstract void sendArguments(PivotSender sender,String cmd);


}
