package com.shards.plugin.inventory.impl;

import com.cryptomorin.xseries.XMaterial;
import com.shards.plugin.ShardsPlugin;
import com.shards.plugin.data.ShopItem;
import com.shards.plugin.inventory.InventoryButton;
import com.shards.plugin.inventory.InventoryGUI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ShopItemListGUI extends InventoryGUI {
    private final ShardsPlugin plugin;

    public ShopItemListGUI(ShardsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null, 54, "§6§lShop Items Editor");
    }

    @Override
    public void decorate(Player player) {
        Map<Integer, ShopItem> items = plugin.getShopManager().getShopItems();

        for (Map.Entry<Integer, ShopItem> entry : items.entrySet()) {
            ShopItem shopItem = entry.getValue();
            int displaySlot = entry.getKey();
            
            if (displaySlot >= 45) continue;

            this.addButton(displaySlot, new InventoryButton()
                    .creator(p -> createItemDisplay(shopItem))
                    .consumer(event -> {
                        Player clicker = (Player) event.getWhoClicked();
                        ShopItemEditorGUI editorGUI = new ShopItemEditorGUI(plugin, shopItem);
                        plugin.getGuiManager().openGUI(editorGUI, clicker);
                    })
            );
        }

        this.addButton(49, new InventoryButton()
                .creator(p -> createItem(XMaterial.GREEN_STAINED_GLASS_PANE, "§a§lCreate New Item", 
                        "§7Click to create a", "§7new shop item"))
                .consumer(event -> {
                    Player clicker = (Player) event.getWhoClicked();
                    
                    ShopItem newItem = new ShopItem();
                    newItem.setId("new_item_" + System.currentTimeMillis());
                    newItem.setSlot(0);
                    newItem.setType("COMMAND");
                    newItem.setMaterial("STONE");
                    newItem.setName("§eNew Item");
                    newItem.setLore(Arrays.asList("§7Cost: §e{cost} {currency}"));
                    newItem.setCost(100);
                    newItem.setCommands(Arrays.asList("give {player} stone 1"));
                    newItem.setAmount(1);
                    
                    ShopItemEditorGUI editorGUI = new ShopItemEditorGUI(plugin, newItem);
                    plugin.getGuiManager().openGUI(editorGUI, clicker);
                })
        );

        this.addButton(53, new InventoryButton()
                .creator(p -> createItem(XMaterial.ARROW, "§e§lBack", 
                        "§7Return to main menu"))
                .consumer(event -> {
                    Player clicker = (Player) event.getWhoClicked();
                    EditorMainGUI mainGUI = new EditorMainGUI(plugin);
                    plugin.getGuiManager().openGUI(mainGUI, clicker);
                })
        );

        super.decorate(player);
    }

    private ItemStack createItemDisplay(ShopItem shopItem) {
        ItemStack item = XMaterial.matchXMaterial(shopItem.getMaterial())
                .map(XMaterial::parseItem)
                .orElse(XMaterial.STONE.parseItem());
        
        if (item == null) item = XMaterial.STONE.parseItem();
        
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§e" + shopItem.getName().replace("&", "§"));
            
            List<String> lore = new ArrayList<>();
            lore.add("§7ID: §f" + shopItem.getId());
            lore.add("§7Slot: §f" + shopItem.getSlot());
            lore.add("§7Cost: §f" + shopItem.getCost());
            lore.add("§7Type: §f" + shopItem.getType());
            lore.add("");
            lore.add("§eClick to edit");
            
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
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