package eu.magicmine.pivot.api.commands.methods.impl;

import eu.magicmine.pivot.Pivot;
import eu.magicmine.pivot.api.commands.annotation.Argument;
import eu.magicmine.pivot.api.commands.annotation.SubCommand;
import eu.magicmine.pivot.api.commands.methods.CommandMethod;
import lombok.Getter;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

@Getter
public class SubCommandMethod extends CommandMethod {

    private final SubCommand info;
    private final Map<Argument,Parameter> parameters;

    public SubCommandMethod(Pivot pivot, Object holder, Method method) {
        super(pivot,holder,method);
        info = method.getAnnotation(SubCommand.class);
        parameters = new HashMap<>();
        for(Parameter parameter: method.getParameters()) {
            if(parameter.isAnnotationPresent(Argument.class)) {
                parameters.put(parameter.getAnnotation(Argument.class),parameter);
            }
        }
    }


}
