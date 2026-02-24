package com.shards.plugin;

import com.shards.plugin.commands.AFKZoneCommand;
import com.shards.plugin.commands.ShardShopCommand;
import com.shards.plugin.commands.ShopEditorCommand;
import com.shards.plugin.commands.ShardsCommand;
import com.shards.plugin.data.DatabaseManager;
import com.shards.plugin.inventory.gui.GUIListener;
import com.shards.plugin.inventory.gui.GUIManager;
import com.shards.plugin.listeners.AFKZoneListener;
import com.shards.plugin.managers.AFKZoneManager;
import com.shards.plugin.managers.ChatInputManager;
import com.shards.plugin.managers.ConfigManager;
import com.shards.plugin.managers.ShopManager;
import com.shards.plugin.managers.ShardsManager;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class ShardsPlugin extends JavaPlugin {
    private ConfigManager configManager;
    private DatabaseManager databaseManager;
    private ShardsManager shardsManager;
    private AFKZoneManager afkZoneManager;

    public AFKZoneManager getAFKZoneManager() {
        return afkZoneManager;
    }
    private ShopManager shopManager;
    private GUIManager guiManager;
    private ChatInputManager chatInputManager;
    private Economy economy;

    @Override
    public void onEnable() {
        this.configManager = new ConfigManager(this);
        this.configManager.loadConfig();

        this.databaseManager = new DatabaseManager(this);
        this.databaseManager.connect();

        this.shardsManager = new ShardsManager(this);
        this.afkZoneManager = new AFKZoneManager(this);
        this.shopManager = new ShopManager(this);
        this.guiManager = new GUIManager();
        this.chatInputManager = new ChatInputManager(this);

        setupVault();
        registerCommands();
        registerListeners();

        this.afkZoneManager.startRewardTask();

        getLogger().info("ShardsPlugin has been enabled!");
    }

    @Override
    public void onDisable() {
        if (this.afkZoneManager != null) {
            this.afkZoneManager.stopRewardTask();
        }

        if (this.databaseManager != null) {
            this.databaseManager.disconnect();
        }

        getLogger().info("ShardsPlugin has been disabled!");
    }

    private void setupVault() {
        if (!this.configManager.isVaultEnabled()) {
            return;
        }

        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            getLogger().warning("Vault not found! Economy features will be disabled.");
            return;
        }

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            getLogger().warning("No economy provider found! Economy features will be disabled.");
            return;
        }

        this.economy = rsp.getProvider();
        getLogger().info("Hooked into Vault economy: " + this.economy.getName());
    }

    private void registerCommands() {
        getCommand("shardshop").setExecutor(new ShardShopCommand(this));
        getCommand("shards").setExecutor(new ShardsCommand(this));
        getCommand("afkzone").setExecutor(new AFKZoneCommand(this));
        getCommand("shopeditor").setExecutor(new ShopEditorCommand(this));
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new GUIListener(this.guiManager), this);
        Bukkit.getPluginManager().registerEvents(new AFKZoneListener(this), this);
        Bukkit.getPluginManager().registerEvents(this.chatInputManager, this);
    }

    public void reload() {
        this.configManager.loadConfig();
        this.shopManager.loadShopItems();
        this.afkZoneManager.loadZones();
    }
}