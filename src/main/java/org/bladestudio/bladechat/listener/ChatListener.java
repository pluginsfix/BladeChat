package org.bladestudio.bladechat.listener;

import org.bladestudio.bladechat.BladeChat;
import org.bladestudio.bladechat.api.BladeChatEvent;
import org.bladestudio.bladechat.util.ChatFilter;
import org.bladestudio.bladechat.util.Messages;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class ChatListener implements Listener {

    protected final BladeChat plugin;

    public ChatListener(BladeChat plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        if (!plugin.getConfig().getBoolean("join-quit-messages.enabled", false)) {
            return;
        }
        String key = event.getPlayer().hasPlayedBefore() ? "join" : "first-join";
        event.setJoinMessage(Messages.get(key, Map.of("%player%", event.getPlayer().getName())));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent event) {
        if (!plugin.getConfig().getBoolean("join-quit-messages.enabled", false)) {
            return;
        }
        event.setQuitMessage(Messages.get("quit", Map.of("%player%", event.getPlayer().getName())));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onKick(PlayerKickEvent event) {
        if (!plugin.getConfig().getBoolean("join-quit-messages.enabled", false)) {
            return;
        }
        event.setLeaveMessage(Messages.get("kick", Map.of("%player%", event.getPlayer().getName())));
    }

    protected void execute(AsyncPlayerChatEvent event) {
        if (!plugin.getConfig().getBoolean("enable", true)) {
            return;
        }

        Player player = event.getPlayer();
        String rawMessage = event.getMessage();

        if (plugin.getConfig().getBoolean("ads.enabled", true)) {
            List<String> bypassList = plugin.getConfig().getStringList("ads.bypass");
            if (ChatFilter.containsAdvertisement(rawMessage, bypassList)) {
                if (!player.hasPermission("bladechat.bypassads")) {
                    event.setCancelled(true);
                    Messages.send(player, "ad-detected");
                    for (Player online : Bukkit.getOnlinePlayers()) {
                        if (online.hasPermission("bladechat.notifyad")) {
                            Messages.sendRaw(online, "ad-notify", Map.of(
                                    "%player%", player.getName(),
                                    "%message%", rawMessage
                            ));
                        }
                    }
                    if (plugin.getConfig().getBoolean("ads.log", true)) {
                        plugin.getLogger().warning("Обнаружена реклама от " + player.getName() + ": " + rawMessage);
                    }
                    return;
                }
            }
        }

        boolean rangedMode = plugin.getConfig().getBoolean("ranged-mode", true);
        boolean isGlobal = false;
        String format;

        if (rangedMode) {
            if (rawMessage.startsWith("!")) {
                isGlobal = true;
                rawMessage = rawMessage.substring(1).trim();
                format = plugin.getConfig().getString("global-message-format", "&8[&6Г&8] %prefix%%player%%suffix%&8: &e%message%");
            } else {
                format = plugin.getConfig().getString("message-format", "%prefix%%player%%suffix%&8: &f%message%");
                double range = plugin.getConfig().getDouble("chat-range", 100.0);
                double rangeSquared = range * range;
                Location senderLocation = player.getLocation();
                List<Player> recipients = new ArrayList<>();
                for (Player recipient : player.getWorld().getPlayers()) {
                    if (senderLocation.distanceSquared(recipient.getLocation()) <= rangeSquared) {
                        recipients.add(recipient);
                    }
                }
                event.getRecipients().clear();
                event.getRecipients().addAll(recipients);
            }
        } else {
            format = plugin.getConfig().getString("message-format", "%prefix%%player%%suffix%&8: &f%message%");
        }

        format = plugin.getHookManager().replacePlaceholders(player, format);
        format = format.replace("%message%", "%2$s").replace("%message", "%2$s");
        format = format.replace("%player%", "%1$s").replace("%player", "%1$s");

        BladeChatEvent bladeChatEvent = new BladeChatEvent(player, format);
        Bukkit.getPluginManager().callEvent(bladeChatEvent);
        format = bladeChatEvent.getFormat();

        event.setFormat(Messages.colorize(format));
        String formattedMessage = ChatFilter.applyPlayerPermissions(rawMessage, player);
        event.setMessage(formattedMessage);

        if (plugin.getConfig().getBoolean("log-chat", true)) {
            String prefix = isGlobal ? "[Global] " : "[Local] ";
            plugin.getLogger().info(prefix + player.getName() + ": " + formattedMessage);
        }
    }
}
