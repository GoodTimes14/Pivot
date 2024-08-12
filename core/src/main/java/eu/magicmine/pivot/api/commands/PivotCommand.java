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
import lombok.Getter;
import lombok.SneakyThrows;

import java.lang.reflect.Method;
import java.util.*;

@Getter
public abstract class PivotCommand extends PivotHolder {

    private DefaultCommandMethod defaultCommand;
    private CommandInfo info;
    private final Map<String, SubCommandMethod> subCommandMap;

    private final Map<String, TabCompletionMethod> tabCompletionMap;

    public PivotCommand(Pivot pivot) {
        super(pivot);
        subCommandMap = new HashMap<>();
        tabCompletionMap = new HashMap<>();
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
            } else if(method.isAnnotationPresent(TabCompletion.class)) {
                TabCompletion info = method.getAnnotation(TabCompletion.class);
                tabCompletionMap.put(info.name(),new TabCompletionMethod(pivot,this,method));
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



    @SneakyThrows
    public List<String> onTabComplete(PivotSender sender, String[] args) {

        CommandMethod method = null;
        List<String> suggestions = new ArrayList<>();
        int current = args.length - 1;


        if(tabCompletionMap.size() != 0) {

            if(current == 0) {
                suggestions.addAll(subCommandMap.keySet());
            } else {
                current -= 1;
                method = tabCompletionMap.get(args[0]);
            }

        }

        if(method == null) {
            return suggestions;
        }

        Argument[] arguments = method.getParameters().keySet().toArray(new Argument[0]);



        if(arguments.length <= current) {
            return suggestions;
        }



        Object[] outInvoke = new Object[method.getParameters().size() + 1];
        outInvoke[0] = method.getSenderClass().cast(sender.getSender());

        boolean valid = true;
        int counter = 1;
        for(int i = 0;i < method.getParameters().size();i++) {
            Argument argument = arguments[i];
            if(counter == args.length) {
                for (int j = i;j < method.getParameters().size();j++) {
                    argument = arguments[j];
                    Class<?> type = method.getParameters().get(argument).getType();
                    if(method.getParameters().get(argument).getType().isPrimitive()) {
                        Converter<?> converter =  pivot.getConversionManager().getConverter(type).orElse(null);
                        if(converter == null) {
                            break;
                        }
                        outInvoke[counter] = converter.nullValue();
                    }
                }
                break;
            }
            Class<?> type = method.getParameters().get(argument).getType();
            if (type.isAssignableFrom(String.class)) {
                outInvoke[counter] = args[counter].length() == 0 ? null : args[counter];
            } else if(type.isAssignableFrom(String[].class)) {
                outInvoke[counter] = Arrays.copyOfRange(args,counter,args.length);
                break;
            } else {
                Optional<Converter<?>> optionalConverter = pivot.getConversionManager().getConverter(type);
                if (optionalConverter.isPresent()) {
                    Converter<?> converter = optionalConverter.get();

                    if(!converter.canConvert(args[counter])) {
                        outInvoke[counter] = null;
                    } else {
                        if(converter instanceof PlayerConverter) {
                            PivotPlayer pivotPlayer = (PivotPlayer) converter.convert(args[counter]);

                            if(pivotPlayer == null) {
                                outInvoke[counter] = null;
                            } else {
                                outInvoke[counter] = pivotPlayer.getSender();
                            }

                        } else {
                            outInvoke[counter] = converter.convert(args[counter]);
                        }
                    }


                }
            }
            counter++;
        }
        suggestions.addAll((Collection<? extends String>) method.getMethod().invoke(method.getHolder(),outInvoke));

        return suggestions;
    }

    public abstract void errorMessage(PivotSender sender,String message);

    public abstract String noPermsMessage();

    public abstract void showHelp(PivotSender sender,CommandMethod method);

    public abstract void sendArguments(PivotSender sender,String cmd);

    public String plugin() { return "pivot"; }



}
