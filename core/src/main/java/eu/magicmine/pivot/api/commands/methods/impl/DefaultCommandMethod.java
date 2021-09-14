package eu.magicmine.pivot.api.commands.methods.impl;

import eu.magicmine.pivot.api.commands.annotation.DefaultCommand;
import eu.magicmine.pivot.api.commands.methods.CommandMethod;

import java.lang.reflect.Method;

public class DefaultCommandMethod extends CommandMethod {


    public DefaultCommand info;

    public DefaultCommandMethod(Object holder,Method method) {
        super(holder,method);
        info = method.getAnnotation(DefaultCommand.class);
    }
}
