package eu.magicmine.pivot.api.server;

import eu.magicmine.pivot.api.server.sender.PivotPlayer;
import eu.magicmine.pivot.api.server.sender.PivotSender;

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

    List<String> getPlayerNames();

    void broadcast(String message);

    void shutdown();

    boolean hasPermission(PivotSender sender, String permission);

}
