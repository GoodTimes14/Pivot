package eu.magicmine.pivot.bungee.server;

import eu.magicmine.pivot.api.server.PivotServer;
import eu.magicmine.pivot.api.server.sender.PivotPlayer;
import eu.magicmine.pivot.api.server.sender.PivotSender;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class PivotBungeeServer implements PivotServer {

    private final ProxyServer server;

    public PivotBungeeServer(ProxyServer server) {
        this.server = server;
    }

    @Override
    public Class<?> getPlayerClass() {
        return ProxiedPlayer.class;
    }

    @Override
    public Class<?> getSenderClass() {
        return CommandSender.class;
    }

    @Override
    public Optional<PivotPlayer> getPlayer(String name) {
        ProxiedPlayer player = server.getPlayer(name);
        return Optional.ofNullable(player == null ? null : new PivotPlayer(player));
    }

    @Override
    public Optional<PivotPlayer> getPlayer(UUID uuid) {
        ProxiedPlayer player = server.getPlayer(uuid);
        return Optional.ofNullable(player == null ? null : new PivotPlayer(player));
    }

    @Override
    public List<PivotPlayer> getPlayers() {
        return server.getPlayers().stream().map(PivotPlayer::new).collect(Collectors.toList());
    }

    @Override
    public List<String> getPlayerNames() {
        return server.getPlayers().stream().map(ProxiedPlayer::getName).collect(Collectors.toList());
    }

    @Override
    public void broadcast(String message) {
        server.broadcast(new TextComponent(message));
    }

    @Override
    public void shutdown() {
        server.stop();
    }

    @Override
    public boolean hasPermission(PivotSender pivotSender, String permission) {
        CommandSender sender = (CommandSender) pivotSender.getSender();
        return sender.hasPermission(permission);
    }
}
