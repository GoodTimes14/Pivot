package eu.magicmine.pivot.api.commands.methods.impl;

import eu.magicmine.pivot.Pivot;
import eu.magicmine.pivot.api.commands.annotation.TabCompletion;
import eu.magicmine.pivot.api.commands.methods.CommandMethod;
import lombok.Getter;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.List;

@Getter
public class TabCompletionMethod extends CommandMethod {

    private final TabCompletion info;

    public TabCompletionMethod(Pivot pivot, Object holder, Method method) {
        this(pivot, holder, method, method.getAnnotation(TabCompletion.class));
    }

    public TabCompletionMethod(Pivot pivot, Object holder, Method method, TabCompletion tabCompletionInfo) {
        super(pivot, holder, method);
        info = tabCompletionInfo;

        if(method.getReturnType().isAssignableFrom(Void.class) ||
                !method.getReturnType().isAssignableFrom(List.class) ||
                !(method.getGenericReturnType() instanceof ParameterizedType) ||
                method.getReturnType().getTypeParameters().length == 0) {

            throw new IllegalArgumentException("The method must return List<String> (" + method.getName() +")");
        }

        ParameterizedType type = (ParameterizedType) method.getGenericReturnType();

        if(!type.getActualTypeArguments()[0].getClass().isInstance(String.class)) {
            throw new IllegalArgumentException("Illegal type, the method must return List<String> (" + method.getName() +")");
        }
    }
}
