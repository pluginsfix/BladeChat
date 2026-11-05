package org.bladestudio.bladechat.listener.priority;

import org.bladestudio.bladechat.BladeChat;
import org.bladestudio.bladechat.listener.ChatListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public final class NORMAL extends ChatListener {

    public NORMAL(BladeChat plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        execute(event);
    }
}
