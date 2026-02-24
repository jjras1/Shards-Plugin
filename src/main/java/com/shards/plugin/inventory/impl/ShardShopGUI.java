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
import java.util.List;
import java.util.Map;

public class ShardShopGUI extends InventoryGUI {
    private final ShardsPlugin plugin;

    public ShardShopGUI(ShardsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    protected Inventory createInventory() {
        String title = plugin.getShopManager().getShopTitle();
        int size = plugin.getShopManager().getShopSize();
        return Bukkit.createInventory(null, size, title);
    }

    @Override
    public void decorate(Player player) {
        Map<Integer, ShopItem> items = plugin.getShopManager().getShopItems();

        for (Map.Entry<Integer, ShopItem> entry : items.entrySet()) {
            int slot = entry.getKey();
            ShopItem shopItem = entry.getValue();

            this.addButton(slot, new InventoryButton()
                    .creator(p -> createItemStack(shopItem, p))
                    .consumer(event -> handlePurchase((Player) event.getWhoClicked(), shopItem))
            );
        }

        super.decorate(player);
    }

    private ItemStack createItemStack(ShopItem shopItem, Player player) {
        ItemStack item;

        if (shopItem.getType().equalsIgnoreCase("SPAWNER")) {
            item = XMaterial.SPAWNER.parseItem();
        } else if (shopItem.getMaterial().equals("*")) {
            item = XMaterial.GRAY_STAINED_GLASS_PANE.parseItem();
        } else {
            item = XMaterial.matchXMaterial(shopItem.getMaterial())
                    .map(XMaterial::parseItem)
                    .orElse(XMaterial.STONE.parseItem());
        }

        if (item == null) {
            item = XMaterial.STONE.parseItem();
        }

        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            String name = shopItem.getName().replace("&", "§");
            meta.setDisplayName(name);

            List<String> lore = new ArrayList<>();
            String currencyName = shopItem.getCost() == 1 ? 
                    plugin.getConfigManager().getCurrencyName() : 
                    plugin.getConfigManager().getCurrencyNamePlural();

            for (String line : shopItem.getLore()) {
                lore.add(line.replace("&", "§")
                        .replace("{cost}", String.valueOf(shopItem.getCost()))
                        .replace("{currency}", currencyName)
                        .replace("{player}", player.getName()));
            }
            meta.setLore(lore);

            item.setItemMeta(meta);
        }

        return item;
    }

    private void handlePurchase(Player player, ShopItem shopItem) {
        long cost = shopItem.getCost();
        String currencyName = cost == 1 ? 
                plugin.getConfigManager().getCurrencyName() : 
                plugin.getConfigManager().getCurrencyNamePlural();

        if (!plugin.getShardsManager().hasBalance(player.getUniqueId(), player.getName(), cost)) {
            String message = plugin.getConfigManager().getMessage("shop.insufficient_funds")
                    .replace("{amount}", String.valueOf(cost))
                    .replace("{currency}", currencyName);
            player.sendMessage(plugin.getConfigManager().getPrefix() + message);
            return;
        }

        if (!plugin.getShardsManager().removeBalance(player.getUniqueId(), player.getName(), cost)) {
            String message = plugin.getConfigManager().getMessage("shop.error");
            player.sendMessage(plugin.getConfigManager().getPrefix() + message);
            return;
        }

        executeCommands(player, shopItem);

        String message = plugin.getConfigManager().getMessage("shop.purchased")
                .replace("{item}", shopItem.getName().replace("&", "§"));
        player.sendMessage(plugin.getConfigManager().getPrefix() + message);

        player.closeInventory();
    }

    private void executeCommands(Player player, ShopItem shopItem) {
        for (String command : shopItem.getCommands()) {
            String cmd = command.replace("{player}", player.getName())
                    .replace("{amount}", String.valueOf(shopItem.getAmount()));
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
        }
    }
}