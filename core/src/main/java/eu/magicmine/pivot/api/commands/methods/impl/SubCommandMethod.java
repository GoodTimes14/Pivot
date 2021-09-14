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
    private final List<Parameter> parameters;

    public SubCommandMethod(Object holder,Method method) {
        super(holder,method);
        info = method.getAnnotation(SubCommand.class);
        parameters = new ArrayList<>();
        for(Parameter parameter: method.getParameters()) {
            if(parameter.isAnnotationPresent(Argument.class)) {
                parameters.add(parameter);
            }
        }
    }


}
