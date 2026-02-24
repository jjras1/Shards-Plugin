package com.shards.plugin.managers;

import com.shards.plugin.ShardsPlugin;
import com.shards.plugin.data.AFKZone;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class AFKZoneManager {
    private final ShardsPlugin plugin;
    private final Map<String, AFKZone> zones;
    private final Map<UUID, AFKZone> playersInZones;
    private final Map<UUID, Long> lastRewardTime;
    private BukkitTask rewardTask;

    public AFKZoneManager(ShardsPlugin plugin) {
        this.plugin = plugin;
        this.zones = new HashMap<>();
        this.playersInZones = new HashMap<>();
        this.lastRewardTime = new HashMap<>();
        loadZones();
    }

    public void loadZones() {
        zones.clear();
        Connection conn = plugin.getDatabaseManager().getConnection();
        String query = "SELECT * FROM afk_zones";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                AFKZone zone = new AFKZone(
                        rs.getString("name"),
                        rs.getString("world"),
                        rs.getInt("min_x"),
                        rs.getInt("min_y"),
                        rs.getInt("min_z"),
                        rs.getInt("max_x"),
                        rs.getInt("max_y"),
                        rs.getInt("max_z"),
                        rs.getInt("reward_per_minute")
                );
                zones.put(zone.getName(), zone);
            }

            plugin.getLogger().info("Loaded " + zones.size() + " AFK zones");
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to load AFK zones: " + e.getMessage());
        }
    }

    public void createZone(String name, Location min, Location max, int rewardPerMinute) {
        AFKZone zone = new AFKZone(
                name,
                min.getWorld().getName(),
                Math.min(min.getBlockX(), max.getBlockX()),
                Math.min(min.getBlockY(), max.getBlockY()),
                Math.min(min.getBlockZ(), max.getBlockZ()),
                Math.max(min.getBlockX(), max.getBlockX()),
                Math.max(min.getBlockY(), max.getBlockY()),
                Math.max(min.getBlockZ(), max.getBlockZ()),
                rewardPerMinute
        );

        Connection conn = plugin.getDatabaseManager().getConnection();
        String query = "INSERT INTO afk_zones (name, world, min_x, min_y, min_z, max_x, max_y, max_z, reward_per_minute) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, zone.getName());
            stmt.setString(2, zone.getWorldName());
            stmt.setInt(3, zone.getMinX());
            stmt.setInt(4, zone.getMinY());
            stmt.setInt(5, zone.getMinZ());
            stmt.setInt(6, zone.getMaxX());
            stmt.setInt(7, zone.getMaxY());
            stmt.setInt(8, zone.getMaxZ());
            stmt.setInt(9, zone.getRewardPerMinute());
            stmt.executeUpdate();

            zones.put(name, zone);
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to create AFK zone: " + e.getMessage());
        }
    }

    public void deleteZone(String name) {
        Connection conn = plugin.getDatabaseManager().getConnection();
        String query = "DELETE FROM afk_zones WHERE name = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.executeUpdate();
            zones.remove(name);
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to delete AFK zone: " + e.getMessage());
        }
    }

    public AFKZone getZone(String name) {
        return zones.get(name);
    }

    public Collection<AFKZone> getAllZones() {
        return zones.values();
    }

    public boolean zoneExists(String name) {
        return zones.containsKey(name);
    }

    public AFKZone getZoneAtLocation(Location location) {
        for (AFKZone zone : zones.values()) {
            if (zone.contains(location)) {
                return zone;
            }
        }
        return null;
    }

    public void checkPlayerZone(Player player) {
        UUID uuid = player.getUniqueId();
        Location location = player.getLocation();
        AFKZone currentZone = getZoneAtLocation(location);
        AFKZone previousZone = playersInZones.get(uuid);

        if (currentZone != null && !currentZone.equals(previousZone)) {
            playersInZones.put(uuid, currentZone);
            lastRewardTime.put(uuid, System.currentTimeMillis());

            String message = plugin.getConfigManager().getMessage("afk.entered_zone")
                    .replace("{zone}", currentZone.getName());
            player.sendMessage(message);
        } else if (currentZone == null && previousZone != null) {
            playersInZones.remove(uuid);
            lastRewardTime.remove(uuid);

            String message = plugin.getConfigManager().getMessage("afk.left_zone")
                    .replace("{zone}", previousZone.getName());
            player.sendMessage(message);
        }
    }

    public void startRewardTask() {
        int interval = plugin.getConfigManager().getAFKCheckInterval();

        this.rewardTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                checkPlayerZone(player);
                rewardPlayer(player);
            }
        }, interval, interval);
    }

    public void stopRewardTask() {
        if (this.rewardTask != null) {
            this.rewardTask.cancel();
        }
    }

    private void rewardPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        AFKZone zone = playersInZones.get(uuid);

        if (zone == null) {
            return;
        }

        if (plugin.getConfigManager().requireAFKPermission() && !player.hasPermission("shards.afk")) {
            return;
        }

        Long lastReward = lastRewardTime.get(uuid);
        if (lastReward == null) {
            lastRewardTime.put(uuid, System.currentTimeMillis());
            return;
        }

        long currentTime = System.currentTimeMillis();
        long timeDiff = currentTime - lastReward;
        int rewardInterval = plugin.getConfigManager().getAFKRewardInterval() * 1000;

        if (timeDiff >= rewardInterval) {
            int reward = zone.getRewardPerMinute();
            plugin.getShardsManager().addBalance(uuid, player.getName(), reward);

            String currencyName = reward == 1 ? 
                    plugin.getConfigManager().getCurrencyName() : 
                    plugin.getConfigManager().getCurrencyNamePlural();

            String message = plugin.getConfigManager().getMessage("afk.reward")
                    .replace("{amount}", String.valueOf(reward))
                    .replace("{currency}", currencyName);
            player.sendMessage(message);

            lastRewardTime.put(uuid, currentTime);
        }
    }

    public void removePlayer(UUID uuid) {
        playersInZones.remove(uuid);
        lastRewardTime.remove(uuid);
    }
}