package com.shards.plugin.commands;

import com.shards.plugin.ShardsPlugin;
import com.shards.plugin.inventory.impl.ShardShopGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShardShopCommand implements CommandExecutor {
    private final ShardsPlugin plugin;

    public ShardShopCommand(ShardsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getConfigManager().getMessage("errors.player_only"));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("shards.shop")) {
            player.sendMessage(plugin.getConfigManager().getMessage("errors.no_permission"));
            return true;
        }

        ShardShopGUI gui = new ShardShopGUI(plugin);
        plugin.getGuiManager().openGUI(gui, player);

        return true;
    }
}