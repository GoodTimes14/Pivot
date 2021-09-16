package eu.magicmine.pivot.velocity.server;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import eu.magicmine.pivot.api.server.PivotServer;
import eu.magicmine.pivot.api.server.sender.PivotPlayer;
import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class PivotVelocityServer implements PivotServer {

    private ProxyServer server;

    public PivotVelocityServer(ProxyServer server) {
        this.server = server;
    }

    @Override
    public Class<?> getPlayerClass() {
        return Player.class;
    }

    @Override
    public Class<?> getSenderClass() {
        return CommandSource.class;
    }

    @Override
    public Optional<PivotPlayer> getPlayer(String name) {
        return Optional.ofNullable(server.getPlayer(name).map(PivotPlayer::new).orElse(null));
    }

    @Override
    public Optional<PivotPlayer> getPlayer(UUID uuid) {
        return Optional.ofNullable(server.getPlayer(uuid).map(PivotPlayer::new).orElse(null));
    }

    @Override
    public List<PivotPlayer> getPlayers() {
        return server.getAllPlayers().stream().map(PivotPlayer::new).collect(Collectors.toList());
    }

    @Override
    public void broadcast(String message) {
        server.sendMessage(Component.text(message));
    }

    @Override
    public void shutdown() {
        server.shutdown();
    }
}
