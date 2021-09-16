package eu.magicmine.pivot.api.commands;

import eu.magicmine.pivot.Pivot;
import eu.magicmine.pivot.api.commands.annotation.CommandInfo;
import eu.magicmine.pivot.api.commands.annotation.DefaultCommand;
import eu.magicmine.pivot.api.commands.annotation.SubCommand;
import eu.magicmine.pivot.api.commands.methods.impl.DefaultCommandMethod;
import eu.magicmine.pivot.api.commands.methods.impl.SubCommandMethod;
import eu.magicmine.pivot.api.conversion.Converter;
import eu.magicmine.pivot.api.conversion.impl.PlayerConverter;
import eu.magicmine.pivot.api.server.sender.PivotPlayer;
import eu.magicmine.pivot.api.server.sender.PivotSender;
import eu.magicmine.pivot.api.utils.PivotHolder;
import lombok.Getter;
import lombok.SneakyThrows;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
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
                defaultCommand = new DefaultCommandMethod(pivot,this,method);
            }
            if(method.isAnnotationPresent(SubCommand.class)) {
                SubCommand info = method.getAnnotation(SubCommand.class);
                subCommandMap.put(info.name(),new SubCommandMethod(pivot,this,method));
            }
        }
    }

    @SneakyThrows
    public void onCommand(PivotSender sender, String cmd, String[] args) {
        if(args.length == 0 && defaultCommand == null) {
            sendArguments(sender,cmd);
            return;
        } else if(args.length == 0 || subCommandMap.size() == 0) {
            defaultCommand.getMethod().invoke(defaultCommand.getHolder(),defaultCommand.getSenderClass().cast(sender.getSender()));
            return;
        }
        SubCommandMethod subCommandMethod = subCommandMap.get(args[0]);
        if(subCommandMethod == null) {
            sendArguments(sender,cmd);
            return;
        }
        Object[] params = new Object[subCommandMethod.getParameters().size() + 1];
        params[0] = defaultCommand.getSenderClass().cast(sender.getSender());
        boolean valid = true;
        if( args.length - 1 < subCommandMethod.getParameters().size() ) {
            showHelp(sender,subCommandMethod);
            return;
        }
        Parameter[] parameters = subCommandMethod.getParameters().values().toArray(new Parameter[0]);
        for(int i = 0;i < parameters.length;i++) {
            Class<?> type = parameters[i].getType();
            if(type.isAssignableFrom(String.class)) {
                params[i + 1] = args[i + 1];
                continue;
            }
            Optional<Converter<?>> optionalConverter = pivot.getConversionManager().getConverter(type);
            if(optionalConverter.isPresent()) {
                Converter<?> converter = optionalConverter.get();
                if(!converter.canConvert(args[i + 1])) {
                    valid = false;
                    showHelp(sender,subCommandMethod);
                    break;
                }
                if(converter instanceof PlayerConverter) {
                    PivotPlayer pivotPlayer = (PivotPlayer) converter.convert(args[i + 1]);
                    if(pivotPlayer == null) {
                        showHelp(sender,subCommandMethod);
                        valid = false;
                        break;
                    }
                    params[i + 1] = pivotPlayer.getSender();
                } else {
                    params[i + 1] = converter.convert(args[i + 1]);
                }
            }
        }
        if(valid) {
            System.out.println(Arrays.toString(params));
            subCommandMethod.getMethod().invoke(subCommandMethod.getHolder(),params);
        }
    }

    public abstract String noPermsMessage();

    public abstract void showHelp(PivotSender sender,SubCommandMethod subCommandMethod);

    public abstract void sendArguments(PivotSender sender,String cmd);


}
