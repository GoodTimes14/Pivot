package eu.magicmine.pivot.api.server;

import eu.magicmine.pivot.api.server.sender.PivotPlayer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


/**
 *
 * @author DeezNuts
 * @apiNote This thing goes wrap,pivot goes brrr and command goes bleep bloop.
 *
 */
public interface PivotServer {

    Class<?> getPlayerClass();

    Class<?> getSenderClass();
    
    Optional<PivotPlayer> getPlayer(String name);

    Optional<PivotPlayer> getPlayer(UUID uuid);

    List<PivotPlayer> getPlayers();

    void broadcast(String message);

    void shutdown();

}
