package eu.magicmine.pivot.spigot.ems.event.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;

@RequiredArgsConstructor
@Getter
public class EventBus<T extends Event> {

    private final T event;

    private final Map<String,Object> objectBus = new HashMap<>();


    public <V> void set(Class<V> clazz,V object) {
        String simpleName = clazz.getName();
        objectBus.put(simpleName,object);
    }

    public <V> Optional<V> get(Class<V> clazz) {

        Object object = objectBus.get(clazz.getName());

        if(object == null) return Optional.empty();

        try {

            return Optional.of(clazz.cast(object));

        } catch (ClassCastException e) {
            Bukkit.getLogger().log(Level.SEVERE,"Can't cast object in eventBus",e);
        }

        return Optional.empty();
    }

}
