package com.shards.plugin.listeners;

import com.shards.plugin.ShardsPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class AFKZoneListener implements Listener {
    private final ShardsPlugin plugin;

    public AFKZoneListener(ShardsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() &&
            event.getFrom().getBlockY() == event.getTo().getBlockY() &&
            event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        plugin.getAFKZoneManager().checkPlayerZone(event.getPlayer());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        plugin.getAFKZoneManager().checkPlayerZone(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getAFKZoneManager().removePlayer(event.getPlayer().getUniqueId());
        plugin.getShardsManager().unloadPlayer(event.getPlayer().getUniqueId());
    }
}