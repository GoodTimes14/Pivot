package eu.magicmine.pivot.api.commands.methods.impl;

import eu.magicmine.pivot.api.commands.annotation.Argument;
import eu.magicmine.pivot.api.commands.annotation.SubCommand;
import eu.magicmine.pivot.api.commands.methods.CommandMethod;
import lombok.Getter;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

@Getter
public class SubCommandMethod extends CommandMethod {

    private final SubCommand info;
    private final Class<?>[] parameterTypes;

    public SubCommandMethod(Method method) {
        super(method);
        info = method.getAnnotation(SubCommand.class);
        List<Class<?>> classList = new ArrayList<>();
        for(Parameter parameter: method.getParameters()) {
            if(parameter.isAnnotationPresent(Argument.class)) {
                classList.add(parameter.getType());
            }
        }
        parameterTypes = classList.toArray(new Class[0]);
    }


}
