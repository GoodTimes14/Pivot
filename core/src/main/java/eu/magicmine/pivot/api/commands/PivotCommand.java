package eu.magicmine.pivot.api.commands;

import eu.magicmine.pivot.Pivot;
import eu.magicmine.pivot.api.commands.annotation.CommandInfo;
import eu.magicmine.pivot.api.commands.annotation.DefaultCommand;
import eu.magicmine.pivot.api.commands.annotation.SubCommand;
import eu.magicmine.pivot.api.commands.methods.CommandMethod;
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

    public void fetchSubCommands(Object object) {
        for(Method method : object.getClass().getDeclaredMethods()) {
            if(method.isAnnotationPresent(SubCommand.class)) {
                method.setAccessible(true);
                SubCommand info = method.getAnnotation(SubCommand.class);
                subCommandMap.put(info.name(),new SubCommandMethod(pivot,object,method));
            }
        }
    }

    public void registerSubCommands() {}


    @SneakyThrows
    public void onCommand(PivotSender sender,String cmd,String[] args) {
        CommandMethod method;
        if(args.length == 0) {
            if(defaultCommand  == null) {
                errorMessage(sender,"You need to specify an argument.");
                sendArguments(sender,cmd);
                return;
            }
            method = defaultCommand;
        } else {
            method = subCommandMap.get(args[0]);
        }
        if(method == null) {
            errorMessage(sender,"Invalid argument.");
            sendArguments(sender,cmd);
            return;
        }
        Object[] outInvoke = new Object[method.getParameters().size() + 1];
        outInvoke[0] = method.getSenderClass().cast(sender.getSender());
        //Start index for args array
        int x = method instanceof SubCommandMethod ? 1 : 0;
        if(args.length - x < method.getParameters().size() ) {
            errorMessage(sender,"Invalid parameters.");
            showHelp(sender,method);
            return;
        }
        Parameter[] parameters = method.getParameters().values().toArray(new Parameter[0]);
        boolean valid = true;
        for(int i = 0;i < method.getParameters().size();i++) {
            Class<?> type = parameters[i].getType();
            if (type.isAssignableFrom(String.class)) {
                outInvoke[i + 1] = args[i + x];
                continue;
            } else if(type.isAssignableFrom(String[].class)) {
                outInvoke[i + 1] = Arrays.copyOfRange(args,i + x,args.length);
                break;
            }
            Optional<Converter<?>> optionalConverter = pivot.getConversionManager().getConverter(type);
            if (optionalConverter.isPresent()) {
                Converter<?> converter = optionalConverter.get();
                if(!converter.canConvert(args[i + x])) {
                    valid = false;
                    errorMessage(sender,"Invalid parameter type,expected: " + type.getSimpleName());
                    showHelp(sender,method);
                    break;
                }

                if(converter instanceof PlayerConverter) {
                    PivotPlayer pivotPlayer = (PivotPlayer) converter.convert(args[i + x]);
                    if(pivotPlayer == null) {
                        errorMessage(sender,"Player not found");
                        showHelp(sender,method);
                        valid = false;
                        break;
                    }
                    outInvoke[i + 1] = pivotPlayer.getSender();
                } else {
                    outInvoke[i + 1] = converter.convert(args[i + x]);
                }
            }
        }
        if(valid) {
            method.getMethod().invoke(method.getHolder(),outInvoke);
        }
    }



    /*


    @SneakyThrows
    public void onCommand(PivotSender sender, String cmd, String[] args) {
        if(args.length == 0 && defaultCommand == null) {
            sendArguments(sender,cmd);
            return;
        } else if(args.length == 0 || subCommandMap.size() == 0) {
            if(defaultCommand.wantArgs) {
                defaultCommand.getMethod().invoke(defaultCommand.getHolder(),defaultCommand.getSenderClass().cast(sender.getSender()),args);
            } else {
                defaultCommand.getMethod().invoke(defaultCommand.getHolder(),defaultCommand.getSenderClass().cast(sender.getSender()));
            }
            return;
        }
        SubCommandMethod subCommandMethod = subCommandMap.get(args[0]);
        if(subCommandMethod == null) {
            sendArguments(sender,cmd);
            return;
        }
        Object[] params = new Object[subCommandMethod.getParameters().size() + 1];
        params[0] = subCommandMethod.getSenderClass().cast(sender.getSender());
        boolean valid = true;
        if( args.length - 1 < subCommandMethod.getParameters().size() ) {
            errorMessage(sender,"Invalid parameters.");
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
                    errorMessage(sender,"Invalid parameter type,expected: " + type.getSimpleName());
                    showHelp(sender,subCommandMethod);
                    break;
                }
                if(converter instanceof PlayerConverter) {
                    PivotPlayer pivotPlayer = (PivotPlayer) converter.convert(args[i + 1]);
                    if(pivotPlayer == null) {
                        errorMessage(sender,"Player not found");
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
            subCommandMethod.getMethod().invoke(subCommandMethod.getHolder(),params);
        }
    }

     */

    public abstract void errorMessage(PivotSender sender,String message);

    public abstract String noPermsMessage();

    public abstract void showHelp(PivotSender sender,CommandMethod method);

    public abstract void sendArguments(PivotSender sender,String cmd);

    public String plugin() { return "pivot"; }


}
