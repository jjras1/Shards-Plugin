package com.shards.plugin.managers;

import com.shards.plugin.ShardsPlugin;
import com.shards.plugin.data.ShopItem;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopManager {
    private final ShardsPlugin plugin;
    private final Map<Integer, ShopItem> shopItems;

    public ShopManager(ShardsPlugin plugin) {
        this.plugin = plugin;
        this.shopItems = new HashMap<>();
        loadShopItems();
    }

    public void loadShopItems() {
        shopItems.clear();
        ConfigurationSection itemsSection = plugin.getConfig().getConfigurationSection("shop.items");

        if (itemsSection == null) {
            return;
        }

        for (String key : itemsSection.getKeys(false)) {
            ConfigurationSection itemSection = itemsSection.getConfigurationSection(key);
            if (itemSection == null) continue;

            if (!itemSection.getBoolean("enabled", true)) {
                continue;
            }

            ShopItem item = new ShopItem();
            item.setId(key);
            item.setSlot(itemSection.getInt("slot", 0));
            item.setType(itemSection.getString("type", "ITEM"));
            item.setMaterial(itemSection.getString("material", "STONE"));
            item.setName(itemSection.getString("name", "Item"));
            item.setLore(itemSection.getStringList("lore"));
            item.setCost(itemSection.getLong("cost", 0));
            item.setCommands(itemSection.getStringList("commands"));
            item.setAmount(itemSection.getInt("amount", 1));
            item.setSpawnerType(itemSection.getString("spawner_type", "PIG"));
            item.setCrateName(itemSection.getString("crate_name", "common"));

            shopItems.put(item.getSlot(), item);
        }

        plugin.getLogger().info("Loaded " + shopItems.size() + " shop items");
    }

    public Map<Integer, ShopItem> getShopItems() {
        return new HashMap<>(shopItems);
    }

    public ShopItem getShopItem(int slot) {
        return shopItems.get(slot);
    }

    public String getShopTitle() {
        return plugin.getConfig().getString("shop.title", "&6&l⚡ Shard Shop").replace("&", "§");
    }

    public int getShopSize() {
        return plugin.getConfig().getInt("shop.size", 54);
    }

    public void saveShopItem(ShopItem item) {
        FileConfiguration config = plugin.getConfig();
        String path = "shop.items." + item.getId();

        config.set(path + ".slot", item.getSlot());
        config.set(path + ".type", item.getType());
        config.set(path + ".material", item.getMaterial());
        config.set(path + ".name", item.getName());
        config.set(path + ".lore", item.getLore());
        config.set(path + ".cost", item.getCost());
        config.set(path + ".commands", item.getCommands());
        config.set(path + ".amount", item.getAmount());
        config.set(path + ".enabled", true);

        if (item.getSpawnerType() != null && !item.getSpawnerType().isEmpty()) {
            config.set(path + ".spawner_type", item.getSpawnerType());
        }
        if (item.getCrateName() != null && !item.getCrateName().isEmpty()) {
            config.set(path + ".crate_name", item.getCrateName());
        }

        saveConfig();
        loadShopItems();
    }

    public void deleteShopItem(String id) {
        FileConfiguration config = plugin.getConfig();
        config.set("shop.items." + id, null);
        saveConfig();
        loadShopItems();
    }

    public void setShopTitle(String title) {
        plugin.getConfig().set("shop.title", title);
        saveConfig();
    }

    public void setShopSize(int size) {
        plugin.getConfig().set("shop.size", size);
        saveConfig();
    }

    private void saveConfig() {
        try {
            plugin.getConfig().save(new File(plugin.getDataFolder(), "config.yml"));
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save config: " + e.getMessage());
        }
    }

    public ShopItem getItemById(String id) {
        for (ShopItem item : shopItems.values()) {
            if (item.getId().equals(id)) {
                return item;
            }
        }
        return null;
    }
}