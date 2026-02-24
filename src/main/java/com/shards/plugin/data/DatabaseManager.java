package com.shards.plugin.data;

import com.shards.plugin.ShardsPlugin;
import lombok.Getter;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Getter
public class DatabaseManager {
    private final ShardsPlugin plugin;
    private Connection connection;

    public DatabaseManager(ShardsPlugin plugin) {
        this.plugin = plugin;
    }

    public void connect() {
        try {
            String type = plugin.getConfigManager().getDatabaseType();

            if (type.equalsIgnoreCase("SQLITE")) {
                File dataFolder = plugin.getDataFolder();
                if (!dataFolder.exists()) {
                    dataFolder.mkdirs();
                }
                String path = dataFolder.getAbsolutePath() + File.separator + "shards.db";
                this.connection = DriverManager.getConnection("jdbc:sqlite:" + path);
            } else if (type.equalsIgnoreCase("MYSQL")) {
                String host = plugin.getConfigManager().getDatabaseHost();
                int port = plugin.getConfigManager().getDatabasePort();
                String database = plugin.getConfigManager().getDatabaseName();
                String username = plugin.getConfigManager().getDatabaseUsername();
                String password = plugin.getConfigManager().getDatabasePassword();

                String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false&autoReconnect=true";
                this.connection = DriverManager.getConnection(url, username, password);
            }

            createTables();
            plugin.getLogger().info("Database connected successfully!");
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to connect to database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void disconnect() {
        if (this.connection != null) {
            try {
                this.connection.close();
                plugin.getLogger().info("Database disconnected!");
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to disconnect from database: " + e.getMessage());
            }
        }
    }

    private void createTables() {
        String createPlayerData = "CREATE TABLE IF NOT EXISTS player_data (" +
                "uuid VARCHAR(36) PRIMARY KEY," +
                "name VARCHAR(16) NOT NULL," +
                "balance BIGINT NOT NULL DEFAULT 0," +
                "last_updated BIGINT NOT NULL" +
                ")";

        String createAFKZones = "CREATE TABLE IF NOT EXISTS afk_zones (" +
                "name VARCHAR(32) PRIMARY KEY," +
                "world VARCHAR(64) NOT NULL," +
                "min_x INT NOT NULL," +
                "min_y INT NOT NULL," +
                "min_z INT NOT NULL," +
                "max_x INT NOT NULL," +
                "max_y INT NOT NULL," +
                "max_z INT NOT NULL," +
                "reward_per_minute INT NOT NULL" +
                ")";

        try (PreparedStatement stmt1 = connection.prepareStatement(createPlayerData);
             PreparedStatement stmt2 = connection.prepareStatement(createAFKZones)) {
            stmt1.executeUpdate();
            stmt2.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to create tables: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        try {
            return this.connection != null && !this.connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}