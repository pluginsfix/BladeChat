package pluginsfix.bladechat.listener;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import pluginsfix.bladechat.config.ChatConfig;
import pluginsfix.bladechat.service.ChatService;
import pluginsfix.bladechat.text.Messages;

public final class ChatListener implements Listener {
    private final ChatConfig config;
    private final Messages messages;
    private final ChatService chatService;

    public ChatListener(ChatConfig config, Messages messages, ChatService chatService) {
        this.config = config;
        this.messages = messages;
        this.chatService = chatService;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        String rawMessage = PlainTextComponentSerializer.plainText().serialize(event.message()).trim();

        if (rawMessage.isEmpty()) {
            return;
        }

        event.setCancelled(true);

        String prefix = config.globalPrefix();
        if (prefix != null && !prefix.isEmpty() && rawMessage.startsWith(prefix)) {
            String content = rawMessage.substring(prefix.length()).trim();
            if (content.isEmpty()) {
                messages.send(player, "empty-message");
                return;
            }
            chatService.sendGlobalMessage(player, content);
            return;
        }

        chatService.sendLocalMessage(player, rawMessage);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        chatService.removePlayer(event.getPlayer().getUniqueId());
    }
}
