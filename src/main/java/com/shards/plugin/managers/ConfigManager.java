package com.shards.plugin.managers;

import com.shards.plugin.ShardsPlugin;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;

@Getter
public class ConfigManager {
    private final ShardsPlugin plugin;
    private FileConfiguration config;

    public ConfigManager(ShardsPlugin plugin) {
        this.plugin = plugin;
    }

    public void loadConfig() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        this.config = plugin.getConfig();
    }

    public boolean debug() {
        return config.getBoolean("debug", false);
    }

    public String getDatabaseType() {
        return config.getString("database.type", "SQLITE");
    }

    public String getDatabaseHost() {
        return config.getString("database.host", "localhost");
    }

    public int getDatabasePort() {
        return config.getInt("database.port", 3306);
    }

    public String getDatabaseName() {
        return config.getString("database.database", "shards");
    }

    public String getDatabaseUsername() {
        return config.getString("database.username", "root");
    }

    public String getDatabasePassword() {
        return config.getString("database.password", "password");
    }

    public boolean isVaultEnabled() {
        return config.getBoolean("integrations.vault", true);
    }

    public boolean isEssentialsXEnabled() {
        return config.getBoolean("integrations.essentialsx", true);
    }

    public boolean isWorldEditEnabled() {
        return config.getBoolean("integrations.worldedit", true);
    }

    public boolean isPlaceholderAPIEnabled() {
        return config.getBoolean("integrations.placeholderapi", true);
    }

    public boolean isSmartSpawnersEnabled() {
        return config.getBoolean("integrations.smart_spawners", true);
    }

    public boolean isBetterSpawnersEnabled() {
        return config.getBoolean("integrations.better_spawners", true);
    }

    public boolean isDonutCratesEnabled() {
        return config.getBoolean("integrations.donut_crates", true);
    }

    public String getCurrencyName() {
        return config.getString("currency.name", "Shard");
    }

    public String getCurrencyNamePlural() {
        return config.getString("currency.name_plural", "Shards");
    }

    public String getCurrencySymbol() {
        return config.getString("currency.symbol", "⚡");
    }

    public int getStartingBalance() {
        return config.getInt("currency.starting_balance", 0);
    }

    public int getAFKCheckInterval() {
        return config.getInt("afk_zones.check_interval", 20);
    }

    public int getAFKRewardInterval() {
        return config.getInt("afk_zones.reward_interval", 60);
    }

    public boolean requireAFKPermission() {
        return config.getBoolean("afk_zones.require_permission", false);
    }

    public String getMessage(String path) {
        String message = config.getString("messages." + path, "");
        return message.replace("&", "§");
    }

    public String getPrefix() {
        return getMessage("prefix");
    }
}