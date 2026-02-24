package com.shards.plugin.managers;

import com.shards.plugin.ShardsPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class ChatInputManager implements Listener {
    private final ShardsPlugin plugin;
    private final Map<UUID, Consumer<String>> awaitingInput = new HashMap<>();

    public ChatInputManager(ShardsPlugin plugin) {
        this.plugin = plugin;
    }

    public void requestInput(Player player, Consumer<String> callback) {
        awaitingInput.put(player.getUniqueId(), callback);
        player.sendMessage(plugin.getConfigManager().getPrefix() + plugin.getConfigManager().getMessage("editor.enter_value"));
    }

    public void cancelInput(Player player) {
        awaitingInput.remove(player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (awaitingInput.containsKey(uuid)) {
            event.setCancelled(true);
            String input = event.getMessage();

            Consumer<String> callback = awaitingInput.remove(uuid);
            
            plugin.getServer().getScheduler().runTask(plugin, () -> callback.accept(input));
        }
    }
}