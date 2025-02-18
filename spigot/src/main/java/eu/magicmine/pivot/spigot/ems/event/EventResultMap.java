package eu.magicmine.pivot.spigot.ems.event;

import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class EventResultMap implements Iterable<EventResult<? extends Event>> {

    private final Map<Class<? extends Event>,EventResult<? extends Event>> resultMap = new HashMap<>();

    @SafeVarargs
    public EventResultMap(EventResult<? extends Event>... results) {
        for (EventResult<? extends Event> result : results) {
            put(result);
        }
    }

    @NotNull
    @Override
    public Iterator<EventResult<? extends Event>> iterator() {
        return resultMap.values().iterator();
    }


    public void put(EventResult<? extends Event> eventResult) {
        resultMap.put(eventResult.getEventClass(),eventResult);
    }

    public EventResult<? extends Event> get(Class<? extends Event> eventClass) {
        return resultMap.get(eventClass);
    }

}
