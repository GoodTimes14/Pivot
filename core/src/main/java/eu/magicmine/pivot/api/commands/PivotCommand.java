package eu.magicmine.pivot.api.commands;

import eu.magicmine.pivot.Pivot;
import eu.magicmine.pivot.api.commands.annotation.*;
import eu.magicmine.pivot.api.commands.methods.CommandMethod;
import eu.magicmine.pivot.api.commands.methods.impl.DefaultCommandMethod;
import eu.magicmine.pivot.api.commands.methods.impl.SubCommandMethod;
import eu.magicmine.pivot.api.commands.methods.impl.TabCompletionMethod;
import eu.magicmine.pivot.api.commands.types.ArgumentType;
import eu.magicmine.pivot.api.conversion.Converter;
import eu.magicmine.pivot.api.conversion.impl.PlayerConverter;
import eu.magicmine.pivot.api.server.sender.PivotPlayer;
import eu.magicmine.pivot.api.server.sender.PivotSender;
import eu.magicmine.pivot.api.utils.PivotHolder;
import eu.magicmine.pivot.api.utils.classes.Primitives;
import lombok.Getter;
import lombok.SneakyThrows;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

@Getter
public abstract class PivotCommand extends PivotHolder {

    private DefaultCommandMethod defaultCommand;
    private TabCompletionMethod defaultTabCompletion;
    private CommandInfo info;
    private final Map<String, SubCommandMethod> subCommandMap;

    private final Map<String, TabCompletionMethod> tabCompletionMap;

    protected PivotCommand(Pivot pivot) {
        super(pivot);
        subCommandMap = new HashMap<>();
        tabCompletionMap = new HashMap<>();

        if(!getClass().isAnnotationPresent(CommandInfo.class)) {
            throw new IllegalStateException("CommandInfo annotation not present.");
        }

        info = getClass().getAnnotation(CommandInfo.class);
        for(Method method : getClass().getDeclaredMethods()) {

            if(defaultCommand == null && method.isAnnotationPresent(DefaultCommand.class)) {
                defaultCommand = new DefaultCommandMethod(pivot, this, method);
            }

            // The DefaultTabCompletion have to be described also with the TabCompletion annotation to get the necessary details
            if (defaultTabCompletion == null && method.isAnnotationPresent(DefaultTabCompletion.class)) {
                defaultTabCompletion = new TabCompletionMethod(pivot, this, method, null);
            }

            if(method.isAnnotationPresent(SubCommand.class)) {
                SubCommand subInfo = method.getAnnotation(SubCommand.class);
                subCommandMap.put(subInfo.name(), new SubCommandMethod(pivot, this, method, subInfo));

            } else if(method.isAnnotationPresent(TabCompletion.class)) {
                TabCompletion tabInfo = method.getAnnotation(TabCompletion.class);
                TabCompletionMethod completionMethod = new TabCompletionMethod(pivot, this, method, tabInfo);
                tabCompletionMap.put(tabInfo.name(), completionMethod);
                // If there are aliases add (or replace) them in the tabCompletionMap
                for (String alias : tabInfo.aliases()) {
                    tabCompletionMap.put(alias, completionMethod);
                }
            }
        }
    }

    public void fetchSubCommands(Object object) {
        for(Method method : object.getClass().getDeclaredMethods()) {
            if(method.isAnnotationPresent(SubCommand.class)) {
                method.setAccessible(true);
                SubCommand subInfo = method.getAnnotation(SubCommand.class);
                subCommandMap.put(subInfo.name(),new SubCommandMethod(pivot,object,method));
            }
        }
    }

    public void registerSubCommands() {}

    @SneakyThrows
    public void onCommand(PivotSender sender,String cmd,String[] args) {

        CommandMethod method = findMethod(sender,cmd,args);
        if (method == null) {
            return;
        }

        if(method instanceof SubCommandMethod subCommandMethod) {
            String permission = subCommandMethod.getInfo().permission();
            if (!permission.isEmpty() && !pivot.getServer().hasPermission(sender,permission)) {
                errorMessage(sender,noPermsMessage());
                return;
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
        for (int i = 0;i < method.getParameters().size();i++) {
            Argument argument = arguments[i];
            Class<?> type = method.getParameters().get(argument).getType();
            if(argument.type() == ArgumentType.LABEL) {
                outInvoke[i + 1] = cmd;
                continue;
            }

            //This happens when the sender doesn't define parameters that are not required
            if (!argument.required() && counter == args.length) {
                if (type.isPrimitive()) {
                    outInvoke[i + 1] = Primitives.getDefaultValue(type);
                    continue;
                } else {
                    break;
                }

            }

            if (type.isAssignableFrom(String.class)) {

                outInvoke[i + 1] = args[counter];

            } else if(type.isAssignableFrom(String[].class)) {

                outInvoke[i + 1] = Arrays.copyOfRange(args,counter,args.length);
                break;
            } else {

                Object converted = convertParameter(sender,method,type,args[counter]);
                if (converted == null) {
                    valid = false;

                } else {
                    outInvoke[i + 1] = converted;

                }
            }
            counter++;
        }
        if(valid) {
            method.getMethod().invoke(method.getHolder(),outInvoke);
        }
    }

    private Object convertParameter(PivotSender sender,CommandMethod method,Class<?> type,String parameter) {
        Optional<Converter<?>> optionalConverter = pivot.getConversionManager().getConverter(type);
        if (optionalConverter.isPresent()) {
            Converter<?> converter = optionalConverter.get();
            if(!converter.canConvert(parameter)) {
                errorMessage(sender,"Parametro non valido, richiesto: " + type.getSimpleName());
                showHelp(sender,method);
            } else if(converter instanceof PlayerConverter) {
                PivotPlayer pivotPlayer = (PivotPlayer) converter.convert(parameter);
                if(pivotPlayer == null) {
                    errorMessage(sender,"Player non trovato");
                    showHelp(sender,method);

                } else {
                    return pivotPlayer.getSender();
                }

            } else {
                return converter.convert(parameter);
            }
        }
        return null;
    }

    private CommandMethod findMethod(PivotSender sender,String cmd,String[] args) {

        CommandMethod method = null;
        if(args.length == 0 || subCommandMap.isEmpty()) {

            if(defaultCommand == null) {
                errorMessage(sender,"Devi specificare un parametro.");
                sendArguments(sender,cmd);
                return method;
            }
            method = defaultCommand;

        } else {
            method = subCommandMap.get(args[0]);
        }
        if(method == null) {

            if(defaultCommand == null) {
                errorMessage(sender,"Parametro non valido.");
                sendArguments(sender,cmd);
            } else {
                method = defaultCommand;
            }
        }
        return method;
    }



    @SuppressWarnings("unchecked")
    @SneakyThrows
    public List<String> onTabComplete(PivotSender sender, String[] args) {

        CommandMethod method = null;
        List<String> suggestions = new ArrayList<>();
        int current = args.length - 1;
        boolean isSubCommand = false;
        int argCounter = 1;

        if (current == 0) {
            String input = args[0].toLowerCase(Locale.ROOT);
            for (String subCmd : subCommandMap.keySet()) {
                if (subCmd.toLowerCase(Locale.ROOT).startsWith(input)) {
                    suggestions.add(subCmd);
                }
            }

        } else if (!tabCompletionMap.isEmpty()) {
            current -= 1;
            method = tabCompletionMap.get(args[0]);
            isSubCommand = subCommandMap.containsKey(args[0]);

            // If the method should complete only for players, reset method to null
            if (method instanceof TabCompletionMethod tabCompletionMethod) {
                TabCompletion tabInfo = tabCompletionMethod.getInfo();
                if (tabInfo.playersOnly() && !pivot.getServer().getPlayerClass().isAssignableFrom(sender.getSender().getClass())) {
                    method = null;
                }
            }
        }

        /*
         * If the default tab completion method is found AND
         *
         * 1) The completion is about the default command
         * OR
         * 2) No subcommand completion method has being found
         *
         * Then add its suggestions.
         */
        if (!isSubCommand && method == null && defaultTabCompletion != null) {
            argCounter = 0; // Include the first argument in the default completion
            method = defaultTabCompletion;
        }

        if (method == null) {
            return suggestions;
        }

        Argument[] arguments = method.getParameters().keySet().toArray(new Argument[0]);
        Parameter lastParameter = method.getParameters().get(arguments[arguments.length - 1]);

        // If the current argument to complete is not handled by the method, return
        // But, if the last argument is a param string array, elaborate normally
        if (arguments.length <= current && !lastParameter.getType().isAssignableFrom(String[].class)) {
            return suggestions;
        }

        Object[] outInvoke = new Object[method.getParameters().size() + 1];
        outInvoke[0] = method.getSenderClass().cast(sender.getSender());

        int paramCounter = 1;
        for (int i = 0; i < method.getParameters().size(); i++) {

            Argument argument = arguments[i];
            Class<?> type = method.getParameters().get(argument).getType();

            if (type.isAssignableFrom(String.class)) {
                outInvoke[paramCounter] = args[argCounter].isEmpty() ? null : args[argCounter];

            } else if (type.isAssignableFrom(String[].class)) {
                outInvoke[paramCounter] = Arrays.copyOfRange(args, argCounter, args.length);
                break;

            } else {
                Optional<Converter<?>> optionalConverter = pivot.getConversionManager().getConverter(type);
                if (optionalConverter.isPresent()) {
                    Converter<?> converter = optionalConverter.get();

                    if (!converter.canConvert(args[argCounter])) {
                        outInvoke[paramCounter] = null;

                    } else {
                        if (converter instanceof PlayerConverter) {
                            PivotPlayer pivotPlayer = (PivotPlayer) converter.convert(args[argCounter]);

                            if (pivotPlayer == null) {
                                outInvoke[paramCounter] = null;
                            } else {
                                outInvoke[paramCounter] = pivotPlayer.getSender();
                            }

                        } else {
                            outInvoke[paramCounter] = converter.convert(args[argCounter]);
                        }
                    }
                }
            }
            paramCounter++;
            argCounter++;
        }

        suggestions.addAll((Collection<String>) method.getMethod().invoke(method.getHolder(),outInvoke));
        return suggestions;
    }

    public abstract void errorMessage(PivotSender sender,String message);

    public abstract String noPermsMessage();

    public abstract void showHelp(PivotSender sender,CommandMethod method);

    public abstract void sendArguments(PivotSender sender,String cmd);

    public String plugin() { return "pivot"; }



}
