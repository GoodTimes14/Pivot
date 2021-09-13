package eu.magicmine.pivot.api.commands.methods.impl;

import eu.magicmine.pivot.api.commands.annotation.DefaultCommand;
import eu.magicmine.pivot.api.commands.methods.CommandMethod;

import java.lang.reflect.Method;

public class DefaultCommandMethod extends CommandMethod {


    public DefaultCommand info;

    public DefaultCommandMethod(Method method) {
        super(method);
        info = method.getAnnotation(DefaultCommand.class);
    }
}
