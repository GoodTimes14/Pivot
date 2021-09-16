package eu.magicmine.pivot.api.commands.methods;

import eu.magicmine.pivot.Pivot;
import lombok.Getter;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

@Getter
public abstract class CommandMethod {

    private final Pivot pivot;
    private Object holder;
    private final Method method;
    private Class<?> senderClass;

    public CommandMethod(Pivot pivot, Object holder, Method method) {
        this.pivot = pivot;
        this.holder = holder;
        this.method = method;
        Parameter parameter = method.getParameters()[0];
        if(!pivot.getServer().getSenderClass().isAssignableFrom(parameter.getType())) {
            throw new IllegalArgumentException("The first method parameter must be the sender (" + method.getName() +")");
        }
        senderClass = parameter.getType();
    }
}
