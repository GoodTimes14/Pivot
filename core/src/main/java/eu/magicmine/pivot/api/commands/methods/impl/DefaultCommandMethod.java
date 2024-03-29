package eu.magicmine.pivot.api.commands.methods.impl;

import eu.magicmine.pivot.Pivot;
import eu.magicmine.pivot.api.commands.annotation.DefaultCommand;
import eu.magicmine.pivot.api.commands.methods.CommandMethod;

import java.lang.reflect.Method;

public class DefaultCommandMethod extends CommandMethod {


    public DefaultCommand info;
    public boolean wantArgs;

    public DefaultCommandMethod(Pivot pivot, Object holder, Method method) {
        super(pivot,holder,method);
        wantArgs = method.getParameters().length == 2 && method.getParameters()[1].getType() == String[].class;
        info = method.getAnnotation(DefaultCommand.class);
    }
}
