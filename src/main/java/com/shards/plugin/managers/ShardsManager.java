package com.shards.plugin.managers;

import com.shards.plugin.ShardsPlugin;
import com.shards.plugin.data.PlayerData;
import org.bukkit.OfflinePlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ShardsManager {
    private final ShardsPlugin plugin;
    private final Map<UUID, PlayerData> cache;

    public ShardsManager(ShardsPlugin plugin) {
        this.plugin = plugin;
        this.cache = new HashMap<>();
    }

    public PlayerData getPlayerData(UUID uuid, String name) {
        if (cache.containsKey(uuid)) {
            return cache.get(uuid);
        }

        PlayerData data = loadPlayerData(uuid);
        if (data == null) {
            data = new PlayerData(uuid, name);
            data.setBalance(plugin.getConfigManager().getStartingBalance());
            savePlayerData(data);
        }

        cache.put(uuid, data);
        return data;
    }

    public PlayerData getPlayerData(OfflinePlayer player) {
        return getPlayerData(player.getUniqueId(), player.getName());
    }

    private PlayerData loadPlayerData(UUID uuid) {
        Connection conn = plugin.getDatabaseManager().getConnection();
        String query = "SELECT * FROM player_data WHERE uuid = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, uuid.toString());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new PlayerData(
                        UUID.fromString(rs.getString("uuid")),
                        rs.getString("name"),
                        rs.getLong("balance"),
                        rs.getLong("last_updated")
                );
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to load player data: " + e.getMessage());
        }

        return null;
    }

    public void savePlayerData(PlayerData data) {
        Connection conn = plugin.getDatabaseManager().getConnection();
        String query = "INSERT OR REPLACE INTO player_data (uuid, name, balance, last_updated) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, data.getUuid().toString());
            stmt.setString(2, data.getName());
            stmt.setLong(3, data.getBalance());
            stmt.setLong(4, System.currentTimeMillis());
            stmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to save player data: " + e.getMessage());
        }
    }

    public long getBalance(UUID uuid, String name) {
        return getPlayerData(uuid, name).getBalance();
    }

    public void setBalance(UUID uuid, String name, long amount) {
        PlayerData data = getPlayerData(uuid, name);
        data.setBalance(Math.max(0, amount));
        savePlayerData(data);
    }

    public void addBalance(UUID uuid, String name, long amount) {
        PlayerData data = getPlayerData(uuid, name);
        data.setBalance(data.getBalance() + amount);
        savePlayerData(data);
    }

    public boolean removeBalance(UUID uuid, String name, long amount) {
        PlayerData data = getPlayerData(uuid, name);
        if (data.getBalance() < amount) {
            return false;
        }
        data.setBalance(data.getBalance() - amount);
        savePlayerData(data);
        return true;
    }

    public boolean hasBalance(UUID uuid, String name, long amount) {
        return getBalance(uuid, name) >= amount;
    }

    public void unloadPlayer(UUID uuid) {
        PlayerData data = cache.remove(uuid);
        if (data != null) {
            savePlayerData(data);
        }
    }
}