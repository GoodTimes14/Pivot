package eu.magicmine.pivot.api.commands.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TabCompletion {


    String name();
    String permission() default "";
    String[] aliases() default {};
    boolean playersOnly() default true;

}