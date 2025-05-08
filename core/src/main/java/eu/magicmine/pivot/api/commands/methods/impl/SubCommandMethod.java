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
        this(pivot, holder, method, method.getAnnotation(SubCommand.class));
    }

    public SubCommandMethod(Pivot pivot, Object holder, Method method, SubCommand subCommandInfo) {
        super(pivot, holder, method);
        info = subCommandInfo;
    }

}
