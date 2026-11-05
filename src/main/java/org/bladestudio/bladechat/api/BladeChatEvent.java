package org.bladestudio.bladechat.api;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public final class BladeChatEvent extends PlayerEvent {

    private static final HandlerList HANDLERS = new HandlerList();
    private String format;

    public BladeChatEvent(Player who, String format) {
        super(who);
        this.format = format;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public void replaceVariable(String variable, String value) {
        this.format = format.replace(variable, value);
    }

    public String getFormat() {
        return this.format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}
