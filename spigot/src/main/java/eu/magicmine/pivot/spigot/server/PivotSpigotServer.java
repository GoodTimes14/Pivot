package eu.magicmine.pivot.spigot.server;

import eu.magicmine.pivot.api.server.PivotServer;
import eu.magicmine.pivot.api.server.sender.PivotPlayer;
import eu.magicmine.pivot.spigot.PivotSpigot;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class PivotSpigotServer implements PivotServer {

    private final PivotSpigot api;

    @Override
    public Class<?> getPlayerClass() {
        return Player.class;
    }

    @Override
    public Class<?> getSenderClass() {
        return CommandSender.class;
    }

    @Override
    public Optional<PivotPlayer> getPlayer(String name) {
        Player player = Bukkit.getPlayer(name);
        return Optional.ofNullable(player == null ? null : new PivotPlayer(player));
    }

    @Override
    public Optional<PivotPlayer> getPlayer(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        return Optional.ofNullable(player == null ? null : new PivotPlayer(player));
    }

    @Override
    public List<PivotPlayer> getPlayers() {
        return Bukkit.getOnlinePlayers().stream().map(PivotPlayer::new).collect(Collectors.toList());
    }

    @Override
    public void broadcast(String message) {
        Bukkit.broadcastMessage(message);
    }

    @Override
    public void shutdown() {
        Bukkit.shutdown();
    }
}
