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

public class ShopItemEditorGUI extends InventoryGUI {
    private final ShardsPlugin plugin;
    private final ShopItem shopItem;

    public ShopItemEditorGUI(ShardsPlugin plugin, ShopItem shopItem) {
        this.plugin = plugin;
        this.shopItem = shopItem;
    }

    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null, 54, "§6§lEdit: " + shopItem.getId());
    }

    @Override
    public void decorate(Player player) {
        this.addButton(10, new InventoryButton()
                .creator(p -> createItem(XMaterial.NAME_TAG, "§e§lEdit ID", 
                        "§7Current: §f" + shopItem.getId(),
                        "",
                        "§eClick to change"))
                .consumer(event -> {
                    Player clicker = (Player) event.getWhoClicked();
                    clicker.closeInventory();
                    plugin.getChatInputManager().requestInput(clicker, input -> {
                        if (input.equalsIgnoreCase("cancel")) {
                            clicker.sendMessage(plugin.getConfigManager().getPrefix() + plugin.getConfigManager().getMessage("editor.cancelled"));
                            return;
                        }
                        shopItem.setId(input);
                        clicker.sendMessage(plugin.getConfigManager().getPrefix() + "§aID updated!");
                        ShopItemEditorGUI gui = new ShopItemEditorGUI(plugin, shopItem);
                        plugin.getGuiManager().openGUI(gui, clicker);
                    });
                })
        );

        this.addButton(11, new InventoryButton()
                .creator(p -> createItem(XMaterial.PAPER, "§e§lEdit Slot", 
                        "§7Current: §f" + shopItem.getSlot(),
                        "",
                        "§eClick to change"))
                .consumer(event -> {
                    Player clicker = (Player) event.getWhoClicked();
                    clicker.closeInventory();
                    plugin.getChatInputManager().requestInput(clicker, input -> {
                        if (input.equalsIgnoreCase("cancel")) {
                            clicker.sendMessage(plugin.getConfigManager().getPrefix() + plugin.getConfigManager().getMessage("editor.cancelled"));
                            return;
                        }
                        try {
                            int slot = Integer.parseInt(input);
                            if (slot < 0 || slot >= 54) {
                                clicker.sendMessage(plugin.getConfigManager().getPrefix() + plugin.getConfigManager().getMessage("editor.invalid_slot"));
                                return;
                            }
                            shopItem.setSlot(slot);
                            clicker.sendMessage(plugin.getConfigManager().getPrefix() + "§aSlot updated!");
                        } catch (NumberFormatException e) {
                            clicker.sendMessage(plugin.getConfigManager().getPrefix() + plugin.getConfigManager().getMessage("editor.invalid_slot"));
                        }
                        ShopItemEditorGUI gui = new ShopItemEditorGUI(plugin, shopItem);
                        plugin.getGuiManager().openGUI(gui, clicker);
                    });
                })
        );

        this.addButton(12, new InventoryButton()
                .creator(p -> createItem(XMaterial.DIAMOND, "§e§lEdit Material", 
                        "§7Current: §f" + shopItem.getMaterial(),
                        "",
                        "§eClick to change"))
                .consumer(event -> {
                    Player clicker = (Player) event.getWhoClicked();
                    clicker.closeInventory();
                    plugin.getChatInputManager().requestInput(clicker, input -> {
                        if (input.equalsIgnoreCase("cancel")) {
                            clicker.sendMessage(plugin.getConfigManager().getPrefix() + plugin.getConfigManager().getMessage("editor.cancelled"));
                            return;
                        }
                        shopItem.setMaterial(input.toUpperCase());
                        clicker.sendMessage(plugin.getConfigManager().getPrefix() + "§aMaterial updated!");
                        ShopItemEditorGUI gui = new ShopItemEditorGUI(plugin, shopItem);
                        plugin.getGuiManager().openGUI(gui, clicker);
                    });
                })
        );

        this.addButton(13, new InventoryButton()
                .creator(p -> createItem(XMaterial.WRITABLE_BOOK, "§e§lEdit Name", 
                        "§7Current: " + shopItem.getName().replace("&", "§"),
                        "",
                        "§eClick to change"))
                .consumer(event -> {
                    Player clicker = (Player) event.getWhoClicked();
                    clicker.closeInventory();
                    plugin.getChatInputManager().requestInput(clicker, input -> {
                        if (input.equalsIgnoreCase("cancel")) {
                            clicker.sendMessage(plugin.getConfigManager().getPrefix() + plugin.getConfigManager().getMessage("editor.cancelled"));
                            return;
                        }
                        shopItem.setName(input);
                        clicker.sendMessage(plugin.getConfigManager().getPrefix() + "§aName updated!");
                        ShopItemEditorGUI gui = new ShopItemEditorGUI(plugin, shopItem);
                        plugin.getGuiManager().openGUI(gui, clicker);
                    });
                })
        );

        this.addButton(14, new InventoryButton()
                .creator(p -> createItem(XMaterial.BOOK, "§e§lEdit Lore", 
                        "§7Current lines: §f" + shopItem.getLore().size(),
                        "",
                        "§7Type lore lines separated by §f|",
                        "§eClick to change"))
                .consumer(event -> {
                    Player clicker = (Player) event.getWhoClicked();
                    clicker.closeInventory();
                    plugin.getChatInputManager().requestInput(clicker, input -> {
                        if (input.equalsIgnoreCase("cancel")) {
                            clicker.sendMessage(plugin.getConfigManager().getPrefix() + plugin.getConfigManager().getMessage("editor.cancelled"));
                            return;
                        }
                        List<String> lore = Arrays.asList(input.split("\\|"));
                        shopItem.setLore(lore);
                        clicker.sendMessage(plugin.getConfigManager().getPrefix() + "§aLore updated!");
                        ShopItemEditorGUI gui = new ShopItemEditorGUI(plugin, shopItem);
                        plugin.getGuiManager().openGUI(gui, clicker);
                    });
                })
        );

        this.addButton(15, new InventoryButton()
                .creator(p -> createItem(XMaterial.GOLD_INGOT, "§e§lEdit Cost", 
                        "§7Current: §f" + shopItem.getCost(),
                        "",
                        "§eClick to change"))
                .consumer(event -> {
                    Player clicker = (Player) event.getWhoClicked();
                    clicker.closeInventory();
                    plugin.getChatInputManager().requestInput(clicker, input -> {
                        if (input.equalsIgnoreCase("cancel")) {
                            clicker.sendMessage(plugin.getConfigManager().getPrefix() + plugin.getConfigManager().getMessage("editor.cancelled"));
                            return;
                        }
                        try {
                            long cost = Long.parseLong(input);
                            shopItem.setCost(cost);
                            clicker.sendMessage(plugin.getConfigManager().getPrefix() + "§aCost updated!");
                        } catch (NumberFormatException e) {
                            clicker.sendMessage(plugin.getConfigManager().getPrefix() + plugin.getConfigManager().getMessage("editor.invalid_cost"));
                        }
                        ShopItemEditorGUI gui = new ShopItemEditorGUI(plugin, shopItem);
                        plugin.getGuiManager().openGUI(gui, clicker);
                    });
                })
        );

        this.addButton(16, new InventoryButton()
                .creator(p -> createItem(XMaterial.COMMAND_BLOCK, "§e§lEdit Commands", 
                        "§7Current commands: §f" + shopItem.getCommands().size(),
                        "",
                        "§7Type commands separated by §f|",
                        "§eClick to change"))
                .consumer(event -> {
                    Player clicker = (Player) event.getWhoClicked();
                    clicker.closeInventory();
                    plugin.getChatInputManager().requestInput(clicker, input -> {
                        if (input.equalsIgnoreCase("cancel")) {
                            clicker.sendMessage(plugin.getConfigManager().getPrefix() + plugin.getConfigManager().getMessage("editor.cancelled"));
                            return;
                        }
                        List<String> commands = Arrays.asList(input.split("\\|"));
                        shopItem.setCommands(commands);
                        clicker.sendMessage(plugin.getConfigManager().getPrefix() + "§aCommands updated!");
                        ShopItemEditorGUI gui = new ShopItemEditorGUI(plugin, shopItem);
                        plugin.getGuiManager().openGUI(gui, clicker);
                    });
                })
        );

        this.addButton(19, new InventoryButton()
                .creator(p -> createItem(XMaterial.COMPASS, "§e§lEdit Type", 
                        "§7Current: §f" + shopItem.getType(),
                        "",
                        "§7Types: COMMAND, SPAWNER, CRATE_KEY",
                        "§eClick to change"))
                .consumer(event -> {
                    Player clicker = (Player) event.getWhoClicked();
                    clicker.closeInventory();
                    plugin.getChatInputManager().requestInput(clicker, input -> {
                        if (input.equalsIgnoreCase("cancel")) {
                            clicker.sendMessage(plugin.getConfigManager().getPrefix() + plugin.getConfigManager().getMessage("editor.cancelled"));
                            return;
                        }
                        shopItem.setType(input.toUpperCase());
                        clicker.sendMessage(plugin.getConfigManager().getPrefix() + "§aType updated!");
                        ShopItemEditorGUI gui = new ShopItemEditorGUI(plugin, shopItem);
                        plugin.getGuiManager().openGUI(gui, clicker);
                    });
                })
        );

        this.addButton(40, new InventoryButton()
                .creator(p -> createItem(XMaterial.LIME_STAINED_GLASS_PANE, "§a§lSave Item", 
                        "§7Click to save this", "§7item to the shop"))
                .consumer(event -> {
                    Player clicker = (Player) event.getWhoClicked();
                    plugin.getShopManager().saveShopItem(shopItem);
                    clicker.sendMessage(plugin.getConfigManager().getPrefix() + plugin.getConfigManager().getMessage("editor.item_saved"));
                    ShopItemListGUI listGUI = new ShopItemListGUI(plugin);
                    plugin.getGuiManager().openGUI(listGUI, clicker);
                })
        );

        this.addButton(44, new InventoryButton()
                .creator(p -> createItem(XMaterial.RED_STAINED_GLASS_PANE, "§c§lDelete Item", 
                        "§7Click to delete", "§7this item"))
                .consumer(event -> {
                    Player clicker = (Player) event.getWhoClicked();
                    plugin.getShopManager().deleteShopItem(shopItem.getId());
                    clicker.sendMessage(plugin.getConfigManager().getPrefix() + plugin.getConfigManager().getMessage("editor.item_deleted"));
                    ShopItemListGUI listGUI = new ShopItemListGUI(plugin);
                    plugin.getGuiManager().openGUI(listGUI, clicker);
                })
        );

        this.addButton(49, new InventoryButton()
                .creator(p -> createItem(XMaterial.ARROW, "§e§lBack", 
                        "§7Return to item list"))
                .consumer(event -> {
                    Player clicker = (Player) event.getWhoClicked();
                    ShopItemListGUI listGUI = new ShopItemListGUI(plugin);
                    plugin.getGuiManager().openGUI(listGUI, clicker);
                })
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