package com.shards.plugin.commands;

import com.shards.plugin.ShardsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShardsCommand implements CommandExecutor {
    private final ShardsPlugin plugin;

    public ShardsCommand(ShardsPlugin plugin) {
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
            case "give":
                return handleGive(sender, args);
            case "set":
                return handleSet(sender, args);
            case "take":
                return handleTake(sender, args);
            case "balance":
            case "bal":
                return handleBalance(sender, args);
            case "reload":
                return handleReload(sender);
            default:
                sendHelp(sender);
                return true;
        }
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§6§l⚡ Shards Commands:");
        sender.sendMessage("§e/shards give <player> <amount> §7- Give shards");
        sender.sendMessage("§e/shards set <player> <amount> §7- Set shards");
        sender.sendMessage("§e/shards take <player> <amount> §7- Take shards");
        sender.sendMessage("§e/shards balance [player] §7- Check balance");
        sender.sendMessage("§e/shards reload §7- Reload config");
    }

    private boolean handleGive(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage("§cUsage: /shards give <player> <amount>");
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        long amount;

        try {
            amount = Long.parseLong(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(plugin.getConfigManager().getMessage("errors.invalid_amount"));
            return true;
        }

        plugin.getShardsManager().addBalance(target.getUniqueId(), target.getName(), amount);

        String currencyName = amount == 1 ? 
                plugin.getConfigManager().getCurrencyName() : 
                plugin.getConfigManager().getCurrencyNamePlural();

        String message = plugin.getConfigManager().getMessage("transaction.given")
                .replace("{amount}", String.valueOf(amount))
                .replace("{currency}", currencyName)
                .replace("{player}", target.getName());
        sender.sendMessage(plugin.getConfigManager().getPrefix() + message);

        if (target.isOnline()) {
            String receivedMsg = plugin.getConfigManager().getMessage("transaction.received")
                    .replace("{amount}", String.valueOf(amount))
                    .replace("{currency}", currencyName)
                    .replace("{player}", sender.getName());
            target.getPlayer().sendMessage(plugin.getConfigManager().getPrefix() + receivedMsg);
        }

        return true;
    }

    private boolean handleSet(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage("§cUsage: /shards set <player> <amount>");
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        long amount;

        try {
            amount = Long.parseLong(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(plugin.getConfigManager().getMessage("errors.invalid_amount"));
            return true;
        }

        plugin.getShardsManager().setBalance(target.getUniqueId(), target.getName(), amount);

        String currencyName = amount == 1 ? 
                plugin.getConfigManager().getCurrencyName() : 
                plugin.getConfigManager().getCurrencyNamePlural();

        String message = plugin.getConfigManager().getMessage("transaction.set")
                .replace("{amount}", String.valueOf(amount))
                .replace("{currency}", currencyName)
                .replace("{player}", target.getName());
        sender.sendMessage(plugin.getConfigManager().getPrefix() + message);

        return true;
    }

    private boolean handleTake(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage("§cUsage: /shards take <player> <amount>");
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        long amount;

        try {
            amount = Long.parseLong(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(plugin.getConfigManager().getMessage("errors.invalid_amount"));
            return true;
        }

        if (!plugin.getShardsManager().removeBalance(target.getUniqueId(), target.getName(), amount)) {
            sender.sendMessage(plugin.getConfigManager().getMessage("transaction.insufficient"));
            return true;
        }

        String currencyName = amount == 1 ? 
                plugin.getConfigManager().getCurrencyName() : 
                plugin.getConfigManager().getCurrencyNamePlural();

        String message = plugin.getConfigManager().getMessage("transaction.taken")
                .replace("{amount}", String.valueOf(amount))
                .replace("{currency}", currencyName)
                .replace("{player}", target.getName());
        sender.sendMessage(plugin.getConfigManager().getPrefix() + message);

        return true;
    }

    private boolean handleBalance(CommandSender sender, String[] args) {
        OfflinePlayer target;

        if (args.length < 2) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(plugin.getConfigManager().getMessage("errors.player_only"));
                return true;
            }
            target = (Player) sender;
        } else {
            target = Bukkit.getOfflinePlayer(args[1]);
        }

        long balance = plugin.getShardsManager().getBalance(target.getUniqueId(), target.getName());
        String currencyName = balance == 1 ? 
                plugin.getConfigManager().getCurrencyName() : 
                plugin.getConfigManager().getCurrencyNamePlural();

        String messageKey = target.equals(sender) ? "balance" : "balance_other";
        String message = plugin.getConfigManager().getMessage(messageKey)
                .replace("{balance}", String.valueOf(balance))
                .replace("{currency}", currencyName)
                .replace("{player}", target.getName());

        sender.sendMessage(plugin.getConfigManager().getPrefix() + message);
        return true;
    }

    private boolean handleReload(CommandSender sender) {
        plugin.reload();
        sender.sendMessage(plugin.getConfigManager().getPrefix() + plugin.getConfigManager().getMessage("reload"));
        return true;
    }
}