package org.bladestudio.bladechat.hook;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public final class HookManager {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    private final VaultHook vaultHook;
    private final boolean placeholderApiPresent;

    public HookManager(Plugin plugin) {
        this.vaultHook = new VaultHook(plugin);
        this.placeholderApiPresent = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
    }

    public String getPrefix(Player player) {
        return vaultHook.getPrefix(player);
    }

    public String getSuffix(Player player) {
        return vaultHook.getSuffix(player);
    }

    public String getGroup(Player player) {
        return vaultHook.getPrimaryGroup(player);
    }

    private static String escapePercent(String text) {
        return text != null ? text.replace("%", "%%") : "";
    }

    public String replacePlaceholders(Player player, String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        String result = text;
        result = result.replace("%prefix%", escapePercent(getPrefix(player)));
        result = result.replace("%suffix%", escapePercent(getSuffix(player)));
        result = result.replace("%group%", escapePercent(getGroup(player)));
        result = result.replace("%world%", escapePercent(player.getWorld().getName()));
        result = result.replace("%time%", LocalTime.now().format(TIME_FORMATTER));

        if (placeholderApiPresent) {
            try {
                result = PlaceholderAPI.setPlaceholders(player, result);
            } catch (Exception ignored) {
            }
        }

        return result;
    }
}
