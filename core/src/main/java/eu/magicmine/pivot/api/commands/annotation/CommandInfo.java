package eu.magicmine.pivot.api.commands.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CommandInfo {

    String name();
    String description() default "";
    String permission() default "";
    boolean playersOnly() default true;

}
