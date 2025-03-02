package eu.magicmine.pivot.spigot.ems.action;

import eu.magicmine.pivot.spigot.ems.event.EventResult;
import eu.magicmine.pivot.spigot.ems.event.data.EventBus;
import org.bukkit.event.Event;

public interface ICustomAction {


    EventResult.Action listen(EventBus<Event> eventBus);


}
