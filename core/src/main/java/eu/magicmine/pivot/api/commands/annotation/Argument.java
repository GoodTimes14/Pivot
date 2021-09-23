package eu.magicmine.pivot.api.commands.annotation;

import eu.magicmine.pivot.api.commands.types.ArgumentType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Argument {

    String name();
    String[] choices() default {};
    boolean required() default true;
    ArgumentType type() default ArgumentType.NORMAL;

}
