package eu.magicmine.pivot.spigot.ems.manager.impl;

import eu.magicmine.pivot.spigot.PivotSpigot;
import eu.magicmine.pivot.spigot.ems.action.ICustomAction;
import eu.magicmine.pivot.spigot.ems.action.scheme.IActionScheme;
import eu.magicmine.pivot.spigot.ems.event.EventResult;
import eu.magicmine.pivot.spigot.ems.event.data.EventBus;
import eu.magicmine.pivot.spigot.ems.manager.IEventManager;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.plugin.RegisteredListener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

@RequiredArgsConstructor
public class EventManager implements IEventManager, Listener {

    private final PivotSpigot plugin;

    private final IActionScheme defaultScheme;

    private final Map<UUID,IActionScheme> playerSchemes = new ConcurrentHashMap<>();


    @Override
    public void register(List<Class<? extends Event>> events) {

        RegisteredListener registeredListener = new RegisteredListener(this, (listener, event) ->
                onEvent(event), EventPriority.HIGHEST, plugin, true);

        for (Class<?> event : events) {

            try {

                HandlerList handlerList = (HandlerList) event.getMethod("getHandlerList").invoke(null);
                handlerList.register(registeredListener);

            } catch (ReflectiveOperationException  e) {
                plugin.getLogger().log(Level.SEVERE,"Reflection error: ",e);
            }

        }

    }

    @Override
    public void setScheme(Player player,IActionScheme scheme) {
        playerSchemes.put(player.getUniqueId(), scheme);
    }

    @Override
    public IActionScheme defaultScheme() {
        return defaultScheme;
    }



    public void onEvent(Event event) {
        IActionScheme actionScheme = defaultScheme();

        @SuppressWarnings("unchecked") //Se non ti sta bene il tuo diritto di parola verrà revocato entro 5 giorni
        EventResult<Event> result = (EventResult<Event>) actionScheme.scheme().get(event.getClass());

        if(result == null) return;

        EventResult.Action action = result.getAction();
        if(action == EventResult.Action.CUSTOM_ACTION) {

            EventBus<Event> eventBus = new EventBus<>(event);
            for (ICustomAction<Event> customAction : result.getActionsPipeline()) {

                EventResult.Action stepAction = customAction.listen(eventBus);
                action = stepAction;

                if (stepAction == EventResult.Action.DENY || stepAction == EventResult.Action.SKIP) {
                    break;
                }

            }


        }

        if (Objects.requireNonNull(action) == EventResult.Action.DENY) {
            denyEvent(event);
        }

    }

    private void denyEvent(Event event) {
        if(event instanceof Cancellable cancellable) {
            cancellable.setCancelled(true);

        } else {
            try {
                Method method = event.getClass().getMethod("setResult",Event.Result.class);
                method.invoke(event,Event.Result.DENY);

            } catch (NoSuchMethodException ignored) {

                //We can ignore this

            } catch (InvocationTargetException | IllegalAccessException e) {
                plugin.getLogger().log(Level.SEVERE,"Error while cancelling event",e);
            }
        }


    }

}
