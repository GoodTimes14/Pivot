package eu.magicmine.pivot.api.commands.methods.impl;

import eu.magicmine.pivot.Pivot;
import eu.magicmine.pivot.api.commands.annotation.TabCompletion;
import eu.magicmine.pivot.api.commands.methods.CommandMethod;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.List;

public class TabCompletionMethod extends CommandMethod {



    private TabCompletion info;

    public TabCompletionMethod(Pivot pivot, Object holder, Method method) {
        super(pivot, holder, method);

        if(method.getReturnType().isAssignableFrom(Void.class) || !method.getReturnType().isAssignableFrom(List.class) || !(method.getGenericReturnType() instanceof ParameterizedType) || method.getReturnType().getTypeParameters().length == 0) {
            throw new IllegalArgumentException("The method must return List<String> (" + method.getName() +")");
        }

        ParameterizedType type = (ParameterizedType) method.getGenericReturnType();

        if(!type.getActualTypeArguments()[0].getClass().isInstance(String.class)) {
            throw new IllegalArgumentException("Weird ass type, the method must return List<String> (" + method.getName() +")");
        }

        info = method.getAnnotation(TabCompletion.class);


    }
}
