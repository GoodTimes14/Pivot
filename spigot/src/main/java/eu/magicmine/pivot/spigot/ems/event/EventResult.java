package eu.magicmine.pivot.spigot.ems.event;

import eu.magicmine.pivot.spigot.ems.action.ICustomAction;
import lombok.Getter;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.LinkedList;

@Getter
public class EventResult<T extends Event> {

    private final Class<? extends Event> eventClass;

    private final Action action;

    private final LinkedList<ICustomAction<T>> actionsPipeline = new LinkedList<>();


    @SafeVarargs
    public EventResult(Class<T> eventClass, Action action, @Nullable ICustomAction<T>... customAction) {
        this.eventClass = eventClass;
        this.action = action;
        actionsPipeline.addAll(Arrays.asList(customAction));

    }

    public EventResult(Class<T> eventClass, Action action) {
        this(eventClass,action,null);
    }

    public EventResult(Class<T> eventClass,  @Nullable ICustomAction<T> customAction) {
        this(eventClass,Action.CUSTOM_ACTION,customAction);
    }

    @Getter
    public enum Action {
        ALLOW, SKIP, DENY, CUSTOM_ACTION;

    }

}
