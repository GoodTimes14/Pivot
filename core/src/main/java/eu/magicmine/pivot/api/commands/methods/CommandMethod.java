package eu.magicmine.pivot.api.commands.methods;

import eu.magicmine.pivot.Pivot;
import eu.magicmine.pivot.api.commands.annotation.Argument;
import lombok.Getter;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.LinkedHashMap;

@Getter
public abstract class CommandMethod {

    private final Object holder;
    private final Method method;
    private final Class<?> senderClass;
    private final LinkedHashMap<Argument,Parameter> parameters;

    public CommandMethod(Pivot pivot, Object holder, Method method) {
        this.holder = holder;
        this.method = method;
        parameters = new LinkedHashMap<>();
        Parameter sender = method.getParameters()[0];
        if(!pivot.getServer().getSenderClass().isAssignableFrom(sender.getType())) {
            throw new IllegalArgumentException("The first method parameter must be the sender (" + method.getName() +")");
        }
        senderClass = sender.getType();
        for(Parameter arg : method.getParameters()) {
            if(arg.isAnnotationPresent(Argument.class)) {
                parameters.put(arg.getAnnotation(Argument.class),arg);
            }
        }
    }
}
