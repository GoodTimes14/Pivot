package eu.magicmine.pivot.api.commands.methods.impl;

import eu.magicmine.pivot.Pivot;
import eu.magicmine.pivot.api.commands.annotation.SubCommand;
import eu.magicmine.pivot.api.commands.methods.CommandMethod;
import lombok.Getter;

import java.lang.reflect.Method;

@Getter
public class SubCommandMethod extends CommandMethod {

    private final SubCommand info;

    public SubCommandMethod(Pivot pivot, Object holder, Method method) {
        super(pivot,holder,method);
        info = method.getAnnotation(SubCommand.class);
    }

}
