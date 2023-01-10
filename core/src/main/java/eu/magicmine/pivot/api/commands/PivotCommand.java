package eu.magicmine.pivot.api.commands;

import eu.magicmine.pivot.Pivot;
import eu.magicmine.pivot.api.commands.annotation.Argument;
import eu.magicmine.pivot.api.commands.annotation.CommandInfo;
import eu.magicmine.pivot.api.commands.annotation.DefaultCommand;
import eu.magicmine.pivot.api.commands.annotation.SubCommand;
import eu.magicmine.pivot.api.commands.methods.CommandMethod;
import eu.magicmine.pivot.api.commands.methods.impl.DefaultCommandMethod;
import eu.magicmine.pivot.api.commands.methods.impl.SubCommandMethod;
import eu.magicmine.pivot.api.commands.types.ArgumentType;
import eu.magicmine.pivot.api.conversion.Converter;
import eu.magicmine.pivot.api.conversion.impl.PlayerConverter;
import eu.magicmine.pivot.api.server.sender.PivotPlayer;
import eu.magicmine.pivot.api.server.sender.PivotSender;
import eu.magicmine.pivot.api.utils.PivotHolder;
import lombok.Getter;
import lombok.SneakyThrows;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

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
        if(args.length == 0 || subCommandMap.size() == 0) {
            if(defaultCommand == null) {
                errorMessage(sender,"Devi specificare un parametro.");
                sendArguments(sender,cmd);
                return;
            }
            method = defaultCommand;
        } else {
            method = subCommandMap.get(args[0]);
        }
        if(method == null) {
            if(defaultCommand == null) {
                errorMessage(sender,"Parametro non valido.");
                sendArguments(sender,cmd);
                return;
            } else {
                method = defaultCommand;
            }
        }
        Object[] outInvoke = new Object[method.getParameters().size() + 1];
        outInvoke[0] = method.getSenderClass().cast(sender.getSender());
        //Start index for args array
        int x = method instanceof SubCommandMethod ? 1 : 0;
        if(args.length - x < method.getParameters().keySet().stream().filter(a -> a.required() && a.type() != ArgumentType.LABEL).count()) {
            errorMessage(sender,"Parametri non validi.");
            showHelp(sender,method);
            if(method instanceof DefaultCommandMethod) {
                sendArguments(sender,cmd);
            }
            return;
        }
        Argument[] arguments = method.getParameters().keySet().toArray(new Argument[0]);
        boolean valid = true;
        int counter = x;
        for(int i = 0;i < method.getParameters().size();i++) {
            Argument argument = arguments[i];
            if(argument.type() == ArgumentType.LABEL) {
                outInvoke[i + 1] = cmd;
                continue;
            }
            if(!argument.required() && counter == args.length) {
                Class<?> type = method.getParameters().get(argument).getType();
                if(method.getParameters().get(argument).getType().isPrimitive()) {
                    Converter<?> converter =  pivot.getConversionManager().getConverter(type).orElse(null);
                    if(converter == null) {
                        break;
                    }
                    outInvoke[i + 1] = converter.nullValue();
                    continue;
                } else {
                    break;
                }

            }
            Class<?> type = method.getParameters().get(argument).getType();
            if (type.isAssignableFrom(String.class)) {
                outInvoke[i + 1] = args[counter];
            } else if(type.isAssignableFrom(String[].class)) {
                outInvoke[i + 1] = Arrays.copyOfRange(args,counter,args.length);
                break;
            } else {
                Optional<Converter<?>> optionalConverter = pivot.getConversionManager().getConverter(type);
                if (optionalConverter.isPresent()) {
                    Converter<?> converter = optionalConverter.get();
                    if(!converter.canConvert(args[counter])) {
                        valid = false;
                        errorMessage(sender,"Parametro non valido, richiesto: " + type.getSimpleName());
                        showHelp(sender,method);
                        break;
                    }
                    if(converter instanceof PlayerConverter) {
                        PivotPlayer pivotPlayer = (PivotPlayer) converter.convert(args[counter]);
                        if(pivotPlayer == null) {
                            errorMessage(sender,"Player non trovato");
                            showHelp(sender,method);
                            valid = false;
                            break;
                        }
                        outInvoke[i + 1] = pivotPlayer.getSender();
                    } else {
                        outInvoke[i + 1] = converter.convert(args[counter]);
                    }
                }
            }
            counter++;
        }
        if(valid) {
            method.getMethod().invoke(method.getHolder(),outInvoke);
        }
    }

    public List<String> onTabComplete(PivotSender pivotSender, String[] args) {

        CommandMethod method;
        List<String> suggestions = new ArrayList<>();
        if(args.length == 0 || subCommandMap.size() == 0) {
            if(defaultCommand == null) {
                return suggestions;
            }
            method = defaultCommand;
        } else {
            method = subCommandMap.get(args[0]);
        }
        if(method == null) {
            if(defaultCommand == null) {
                return suggestions;
            } else {
                method = defaultCommand;
            }
        }

        int current = args.length - 1;
        Argument[] arguments = method.getParameters().keySet().toArray(new Argument[0]);
        if(arguments.length <= current) {
            return suggestions;
        }
        Argument argument = arguments[current];

        Parameter parameter = method.getParameters().get(argument);

        if(argument.choices().length != 0) {
            for (String choice : argument.choices()) {
                if(choice.toLowerCase().startsWith(args[current].toLowerCase())) {
                    suggestions.add(choice);
                }
            }
        }

        if(parameter.getType()  == pivot.getServer().getSenderClass()) {
            for (String playerName : pivot.getServer().getPlayerNames()) {
                if(playerName.toLowerCase().startsWith(args[current].toLowerCase())) {
                    suggestions.add(playerName);
                }
            }
        }


        return suggestions;
    }

    public abstract void errorMessage(PivotSender sender,String message);

    public abstract String noPermsMessage();

    public abstract void showHelp(PivotSender sender,CommandMethod method);

    public abstract void sendArguments(PivotSender sender,String cmd);

    public String plugin() { return "pivot"; }



}
