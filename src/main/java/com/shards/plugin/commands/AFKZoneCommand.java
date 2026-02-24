package com.shards.plugin.commands;

import com.shards.plugin.ShardsPlugin;
import com.shards.plugin.data.AFKZone;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AFKZoneCommand implements CommandExecutor {
    private final ShardsPlugin plugin;

    public AFKZoneCommand(ShardsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("shards.admin")) {
            sender.sendMessage(plugin.getConfigManager().getMessage("errors.no_permission"));
            return true;
        }

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "create":
                return handleCreate(sender, args);
            case "delete":
                return handleDelete(sender, args);
            case "list":
                return handleList(sender);
            case "info":
                return handleInfo(sender, args);
            default:
                sendHelp(sender);
                return true;
        }
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§6§l⚡ AFK Zone Commands:");
        sender.sendMessage("§e/afkzone create <name> <reward> §7- Create zone");
        sender.sendMessage("§e/afkzone delete <name> §7- Delete zone");
        sender.sendMessage("§e/afkzone list §7- List all zones");
        sender.sendMessage("§e/afkzone info <name> §7- Zone information");
    }

    private boolean handleCreate(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getConfigManager().getMessage("errors.player_only"));
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage("§cUsage: /afkzone create <name> <reward>");
            return true;
        }

        Player player = (Player) sender;
        String name = args[1];
        int reward;

        try {
            reward = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(plugin.getConfigManager().getMessage("errors.invalid_amount"));
            return true;
        }

        if (plugin.getAFKZoneManager().zoneExists(name)) {
            String message = plugin.getConfigManager().getMessage("zone.already_exists")
                    .replace("{zone}", name);
            sender.sendMessage(plugin.getConfigManager().getPrefix() + message);
            return true;
        }

        try {
            LocalSession localSession = WorldEdit.getInstance().getSessionManager()
                    .get(BukkitAdapter.adapt(player));
            Region selection = localSession.getSelection(BukkitAdapter.adapt(player.getWorld()));

            if (selection == null) {
                sender.sendMessage(plugin.getConfigManager().getMessage("zone.no_selection"));
                return true;
            }

            BlockVector3 min = selection.getMinimumPoint();
            BlockVector3 max = selection.getMaximumPoint();

            Location minLoc = new Location(player.getWorld(), min.getX(), min.getY(), min.getZ());
            Location maxLoc = new Location(player.getWorld(), max.getX(), max.getY(), max.getZ());

            plugin.getAFKZoneManager().createZone(name, minLoc, maxLoc, reward);

            String message = plugin.getConfigManager().getMessage("zone.created")
                    .replace("{zone}", name);
            sender.sendMessage(plugin.getConfigManager().getPrefix() + message);

        } catch (IncompleteRegionException e) {
            sender.sendMessage(plugin.getConfigManager().getMessage("zone.no_selection"));
        }

        return true;
    }

    private boolean handleDelete(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cUsage: /afkzone delete <name>");
            return true;
        }

        String name = args[1];

        if (!plugin.getAFKZoneManager().zoneExists(name)) {
            String message = plugin.getConfigManager().getMessage("zone.not_found")
                    .replace("{zone}", name);
            sender.sendMessage(plugin.getConfigManager().getPrefix() + message);
            return true;
        }

        plugin.getAFKZoneManager().deleteZone(name);

        String message = plugin.getConfigManager().getMessage("zone.deleted")
                .replace("{zone}", name);
        sender.sendMessage(plugin.getConfigManager().getPrefix() + message);

        return true;
    }

    private boolean handleList(CommandSender sender) {
        sender.sendMessage(plugin.getConfigManager().getMessage("zone.list_header"));

        for (AFKZone zone : plugin.getAFKZoneManager().getAllZones()) {
            String message = plugin.getConfigManager().getMessage("zone.list_entry")
                    .replace("{zone}", zone.getName())
                    .replace("{world}", zone.getWorldName())
                    .replace("{reward}", String.valueOf(zone.getRewardPerMinute()));
            sender.sendMessage(message);
        }

        return true;
    }

    private boolean handleInfo(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cUsage: /afkzone info <name>");
            return true;
        }

        String name = args[1];
        AFKZone zone = plugin.getAFKZoneManager().getZone(name);

        if (zone == null) {
            String message = plugin.getConfigManager().getMessage("zone.not_found")
                    .replace("{zone}", name);
            sender.sendMessage(plugin.getConfigManager().getPrefix() + message);
            return true;
        }

        String currencyName = plugin.getConfigManager().getCurrencyNamePlural();

        sender.sendMessage(plugin.getConfigManager().getMessage("zone.info_header")
                .replace("{zone}", zone.getName()));
        sender.sendMessage(plugin.getConfigManager().getMessage("zone.info_world")
                .replace("{world}", zone.getWorldName()));
        sender.sendMessage(plugin.getConfigManager().getMessage("zone.info_reward")
                .replace("{reward}", String.valueOf(zone.getRewardPerMinute()))
                .replace("{currency}", currencyName));
        sender.sendMessage(plugin.getConfigManager().getMessage("zone.info_bounds")
                .replace("{min}", zone.getMinX() + "," + zone.getMinY() + "," + zone.getMinZ())
                .replace("{max}", zone.getMaxX() + "," + zone.getMaxY() + "," + zone.getMaxZ()));

        return true;
    }
}