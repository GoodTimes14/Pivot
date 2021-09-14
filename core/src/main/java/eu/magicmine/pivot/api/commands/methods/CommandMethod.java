package eu.magicmine.pivot.api.commands.methods;

import eu.magicmine.pivot.api.commands.annotation.Sender;
import lombok.Getter;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

@Getter
public abstract class CommandMethod {

    private Object holder;
    private final Method method;
    private Class<?> senderClass;

    public CommandMethod(Object holder,Method method) {
        this.holder = holder;
        this.method = method;
        for(Parameter parameter : method.getParameters()) {
            if(parameter.isAnnotationPresent(Sender.class)) {
                senderClass = parameter.getType();
                break;
            }
        }
        if(senderClass == null) {
            throw new IllegalArgumentException("You need to insert the @Sender before the sender parameter in the method (" + method.getName() +")");
        }
    }
}
