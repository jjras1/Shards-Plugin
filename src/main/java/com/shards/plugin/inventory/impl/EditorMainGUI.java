package com.shards.plugin.inventory.impl;

import com.cryptomorin.xseries.XMaterial;
import com.shards.plugin.ShardsPlugin;
import com.shards.plugin.inventory.InventoryButton;
import com.shards.plugin.inventory.InventoryGUI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class EditorMainGUI extends InventoryGUI {
    private final ShardsPlugin plugin;

    public EditorMainGUI(ShardsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null, 27, "§6§lShop Editor");
    }

    @Override
    public void decorate(Player player) {
        this.addButton(11, new InventoryButton()
                .creator(p -> createItem(XMaterial.CHEST, "§e§lEdit Shop Items", 
                        "§7Click to edit", "§7shop items"))
                .consumer(event -> {
                    Player clicker = (Player) event.getWhoClicked();
                    ShopItemListGUI listGUI = new ShopItemListGUI(plugin);
                    plugin.getGuiManager().openGUI(listGUI, clicker);
                })
        );

        this.addButton(13, new InventoryButton()
                .creator(p -> createItem(XMaterial.NAME_TAG, "§e§lShop Settings", 
                        "§7Current Title: §f" + plugin.getShopManager().getShopTitle(),
                        "§7Current Size: §f" + plugin.getShopManager().getShopSize(),
                        "",
                        "§cComing soon..."))
                .consumer(event -> {})
        );

        this.addButton(15, new InventoryButton()
                .creator(p -> createItem(XMaterial.BARRIER, "§c§lClose", 
                        "§7Close this menu"))
                .consumer(event -> event.getWhoClicked().closeInventory())
        );

        super.decorate(player);
    }

    private ItemStack createItem(XMaterial material, String name, String... lore) {
        ItemStack item = material.parseItem();
        if (item == null) item = XMaterial.STONE.parseItem();
        
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(Arrays.asList(lore));
            item.setItemMeta(meta);
        }
        return item;
    }
}