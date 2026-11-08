package pluginsfix.bladechat.service;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import pluginsfix.bladechat.config.ChatConfig;
import pluginsfix.bladechat.hook.LuckPermsHook;
import pluginsfix.bladechat.hook.PlaceholderHook;
import pluginsfix.bladechat.text.Messages;
import pluginsfix.bladechat.text.TextFormatter;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ChatService {
    private volatile ChatConfig config;
    private final Messages messages;
    private final PlaceholderHook placeholderHook;
    private final LuckPermsHook luckPermsHook;

    private final Map<UUID, Long> globalCooldowns = new ConcurrentHashMap<>();
    private final Map<UUID, ChatChannel> playerChannels = new ConcurrentHashMap<>();
    private final Set<UUID> spyPlayers = ConcurrentHashMap.newKeySet();
    private volatile boolean chatMuted = false;

    public ChatService(ChatConfig config, Messages messages, PlaceholderHook placeholderHook, LuckPermsHook luckPermsHook) {
        this.config = config;
        this.messages = messages;
        this.placeholderHook = placeholderHook;
        this.luckPermsHook = luckPermsHook;
    }

    public void updateConfig(ChatConfig config) {
        this.config = config;
    }

    public ChatConfig getConfig() {
        return config;
    }

    public boolean isChatMuted() {
        return chatMuted;
    }

    public void setChatMuted(boolean chatMuted) {
        this.chatMuted = chatMuted;
    }

    public boolean toggleChatMuted() {
        this.chatMuted = !this.chatMuted;
        return this.chatMuted;
    }

    public ChatChannel getChannel(UUID uuid) {
        return playerChannels.getOrDefault(uuid, ChatChannel.LOCAL);
    }

    public void setChannel(UUID uuid, ChatChannel channel) {
        playerChannels.put(uuid, channel);
    }

    public boolean isSpy(UUID uuid) {
        return spyPlayers.contains(uuid);
    }

    public boolean toggleSpy(UUID uuid) {
        if (spyPlayers.contains(uuid)) {
            spyPlayers.remove(uuid);
            return false;
        } else {
            spyPlayers.add(uuid);
            return true;
        }
    }

    public void removePlayer(UUID uuid) {
        globalCooldowns.remove(uuid);
        playerChannels.remove(uuid);
        spyPlayers.remove(uuid);
    }

    public boolean isOnCooldown(Player player) {
        if (player.hasPermission("bladechat.bypass.cooldown")) {
            return false;
        }
        Long lastTime = globalCooldowns.get(player.getUniqueId());
        if (lastTime == null) {
            return false;
        }
        long diff = System.currentTimeMillis() - lastTime;
        return diff < (config.globalCooldownSeconds() * 1000L);
    }

    public long getCooldownRemainingSeconds(Player player) {
        Long lastTime = globalCooldowns.get(player.getUniqueId());
        if (lastTime == null) {
            return 0;
        }
        long passed = System.currentTimeMillis() - lastTime;
        long total = config.globalCooldownSeconds() * 1000L;
        if (passed >= total) {
            return 0;
        }
        return (total - passed + 999) / 1000;
    }

    public void applyCooldown(Player player) {
        if (!player.hasPermission("bladechat.bypass.cooldown") && config.globalCooldownSeconds() > 0) {
            globalCooldowns.put(player.getUniqueId(), System.currentTimeMillis());
        }
    }

    public String buildFormattedString(Player player, String template, String message) {
        String processedMessage = message;
        if (config.allowColorCodes() && player.hasPermission(config.colorPermission())) {
            processedMessage = TextFormatter.translateHex(processedMessage);
        } else {
            processedMessage = TextFormatter.stripColors(processedMessage);
        }

        String result = template.replace("%message%", processedMessage);
        result = result.replace("%player%", player.getName());

        String prefix = luckPermsHook.getPrefix(player);
        String suffix = luckPermsHook.getSuffix(player);
        result = result.replace("%luckperms_prefix%", prefix);
        result = result.replace("%luckperms_suffix%", suffix);

        result = placeholderHook.parse(player, result);
        return result;
    }

    public Component buildMessageComponent(Player player, String template, String message) {
        String formatted = buildFormattedString(player, template, message);
        return TextFormatter.format(formatted);
    }

    public void sendGlobalMessage(Player player, String message) {
        if (isChatMuted() && !player.hasPermission("bladechat.bypass.mute")) {
            messages.send(player, "chat-is-muted");
            return;
        }
        if (config.globalPermissionRequired() && !player.hasPermission("bladechat.global")) {
            messages.send(player, "global-no-permission");
            return;
        }
        if (isOnCooldown(player)) {
            long remaining = getCooldownRemainingSeconds(player);
            messages.send(player, "cooldown", "%seconds%", String.valueOf(remaining));
            return;
        }
        applyCooldown(player);

        Component component = buildMessageComponent(player, config.globalMessageFormat(), message);
        Bukkit.broadcast(component);
    }

    public void sendLocalMessage(Player player, String message) {
        if (isChatMuted() && !player.hasPermission("bladechat.bypass.mute")) {
            messages.send(player, "chat-is-muted");
            return;
        }

        Component component = buildMessageComponent(player, config.localMessageFormat(), message);
        Location senderLoc = player.getLocation();
        double radiusSq = config.localRadius() * config.localRadius();
        int recipientsCount = 0;

        for (Player target : Bukkit.getOnlinePlayers()) {
            if (target.getWorld().equals(senderLoc.getWorld())) {
                if (target.getLocation().distanceSquared(senderLoc) <= radiusSq) {
                    target.sendMessage(component);
                    if (!target.equals(player)) {
                        recipientsCount++;
                    }
                    continue;
                }
            }
            if (isSpy(target.getUniqueId()) && !target.equals(player)) {
                String spyText = config.spyFormat()
                        .replace("%player%", player.getName())
                        .replace("%message%", message);
                target.sendMessage(TextFormatter.format(spyText));
            }
        }

        if (config.spyConsole()) {
            Bukkit.getConsoleSender().sendMessage(component);
        }

        if (recipientsCount == 0) {
            messages.send(player, "no-one-heard");
        }
    }
}
