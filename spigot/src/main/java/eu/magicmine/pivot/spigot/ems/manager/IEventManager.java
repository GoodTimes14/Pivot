package eu.magicmine.pivot.spigot.ems.manager;

import eu.magicmine.pivot.spigot.ems.action.scheme.IActionScheme;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.List;

public interface IEventManager {


    void register(List<Class<? extends Event>> events);

    void setScheme(Player player,IActionScheme scheme);

    IActionScheme defaultScheme();

}

