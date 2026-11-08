package pluginsfix.bladechat.listener;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import pluginsfix.bladechat.config.ChatConfig;
import pluginsfix.bladechat.service.ChatChannel;
import pluginsfix.bladechat.service.ChatService;
import pluginsfix.bladechat.text.Messages;
import pluginsfix.bladechat.text.TextFormatter;

public final class ChatListener implements Listener {
    private final Messages messages;
    private final ChatService chatService;

    public ChatListener(Messages messages, ChatService chatService) {
        this.messages = messages;
        this.chatService = chatService;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        String rawMessage = PlainTextComponentSerializer.plainText().serialize(event.message()).trim();

        if (rawMessage.isEmpty()) {
            event.setCancelled(true);
            return;
        }

        if (chatService.isChatMuted() && !player.hasPermission("bladechat.bypass.mute")) {
            event.setCancelled(true);
            messages.send(player, "chat-is-muted");
            return;
        }

        ChatConfig config = chatService.getConfig();
        String prefix = config.globalPrefix();
        boolean isGlobal = false;
        String content = rawMessage;

        if (prefix != null && !prefix.isEmpty() && rawMessage.startsWith(prefix)) {
            isGlobal = true;
            content = rawMessage.substring(prefix.length()).trim();
        } else if (chatService.getChannel(player.getUniqueId()) == ChatChannel.GLOBAL) {
            isGlobal = true;
        }

        if (content.isEmpty()) {
            event.setCancelled(true);
            messages.send(player, "empty-message");
            return;
        }

        if (isGlobal) {
            if (config.globalPermissionRequired() && !player.hasPermission("bladechat.global")) {
                event.setCancelled(true);
                messages.send(player, "global-no-permission");
                return;
            }
            if (chatService.isOnCooldown(player)) {
                event.setCancelled(true);
                long remaining = chatService.getCooldownRemainingSeconds(player);
                messages.send(player, "cooldown", "%seconds%", String.valueOf(remaining));
                return;
            }
            chatService.applyCooldown(player);
            Component globalComponent = chatService.buildMessageComponent(player, config.globalMessageFormat(), content);
            event.renderer((source, sourceDisplayName, msg, viewer) -> globalComponent);
            return;
        }

        Location senderLoc = player.getLocation();
        double radiusSq = config.localRadius() * config.localRadius();
        Component localComponent = chatService.buildMessageComponent(player, config.localMessageFormat(), content);
        String spyText = config.spyFormat().replace("%player%", player.getName()).replace("%message%", content);
        Component spyComponent = TextFormatter.format(spyText);

        boolean[] anyoneHeard = new boolean[]{false};
        event.viewers().removeIf(audience -> {
            if (audience instanceof Player target) {
                if (target.equals(player)) {
                    return false;
                }
                if (target.getWorld().equals(senderLoc.getWorld())) {
                    if (target.getLocation().distanceSquared(senderLoc) <= radiusSq) {
                        anyoneHeard[0] = true;
                        return false;
                    }
                }
                if (chatService.isSpy(target.getUniqueId())) {
                    return false;
                }
                return true;
            }
            return !config.spyConsole();
        });

        if (!anyoneHeard[0]) {
            messages.send(player, "no-one-heard");
        }

        event.renderer((source, sourceDisplayName, msg, viewer) -> {
            if (viewer instanceof Player target && !target.equals(player)) {
                if (!target.getWorld().equals(senderLoc.getWorld()) || target.getLocation().distanceSquared(senderLoc) > radiusSq) {
                    if (chatService.isSpy(target.getUniqueId())) {
                        return spyComponent;
                    }
                }
            }
            return localComponent;
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        chatService.removePlayer(event.getPlayer().getUniqueId());
    }
}
