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

    private final LinkedList<ICustomAction> actionsPipeline = new LinkedList<>();



    public EventResult(Class<T> eventClass, Action action, @Nullable ICustomAction... customActions) {
        this.eventClass = eventClass;
        this.action = action;
        actionsPipeline.addAll(Arrays.asList(customActions));

    }

    public EventResult(Class<T> eventClass, @Nullable ICustomAction... customActions) {
        this(eventClass,Action.CUSTOM_ACTION,customActions);
    }

    public EventResult(Class<T> eventClass, Action action) {
        this(eventClass,action,new ICustomAction[0]);
    }

    public EventResult(Class<T> eventClass,  @Nullable ICustomAction customAction) {
        this(eventClass,Action.CUSTOM_ACTION,customAction);
    }

    @Getter
    public enum Action {
        ALLOW, SKIP, DENY, CUSTOM_ACTION;

    }

}
