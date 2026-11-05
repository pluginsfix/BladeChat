package org.bladestudio.bladechat.hook;

import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServiceRegisterEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

public final class VaultHook implements Listener {

    private final Plugin plugin;
    private Chat chatProvider;

    public VaultHook(Plugin plugin) {
        this.plugin = plugin;
        tryHook();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    private void tryHook() {
        if (this.chatProvider != null) {
            return;
        }
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            return;
        }
        RegisteredServiceProvider<Chat> rsp = Bukkit.getServicesManager().getRegistration(Chat.class);
        if (rsp != null) {
            this.chatProvider = rsp.getProvider();
        }
    }

    @EventHandler
    public void onServiceRegister(ServiceRegisterEvent event) {
        if (event.getProvider().getService().equals(Chat.class)) {
            tryHook();
        }
    }

    public boolean isAvailable() {
        if (chatProvider == null) {
            tryHook();
        }
        return chatProvider != null;
    }

    public String getPrefix(Player player) {
        if (!isAvailable()) {
            return "";
        }
        String prefix = chatProvider.getPlayerPrefix(player);
        return prefix != null ? prefix : "";
    }

    public String getSuffix(Player player) {
        if (!isAvailable()) {
            return "";
        }
        String suffix = chatProvider.getPlayerSuffix(player);
        return suffix != null ? suffix : "";
    }

    public String getPrimaryGroup(Player player) {
        if (!isAvailable()) {
            return "";
        }
        try {
            String group = chatProvider.getPrimaryGroup(player);
            return group != null ? group : "";
        } catch (Exception ignored) {
            return "";
        }
    }
}
