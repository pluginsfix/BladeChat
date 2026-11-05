package org.bladestudio.bladechat.util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Collections;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Messages {

    private static final Pattern HEX_PATTERN = Pattern.compile("(?:&#|<#|\\{#)([A-Fa-f0-9]{6})(?:>|\\})?");
    private static JavaPlugin pluginInstance;
    private static File messagesFile;
    private static YamlConfiguration messagesConfig;

    private Messages() {}

    public static void init(JavaPlugin plugin) {
        pluginInstance = plugin;
        messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        reload();
    }

    public static void reload() {
        if (!messagesFile.exists()) {
            pluginInstance.saveResource("messages.yml", false);
        }
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public static String colorize(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        Matcher matcher = HEX_PATTERN.matcher(text);
        StringBuilder buffer = new StringBuilder(text.length() + 32);
        while (matcher.find()) {
            String hex = matcher.group(1);
            matcher.appendReplacement(buffer, "§x§" + hex.charAt(0) + "§" + hex.charAt(1)
                    + "§" + hex.charAt(2) + "§" + hex.charAt(3)
                    + "§" + hex.charAt(4) + "§" + hex.charAt(5));
        }
        matcher.appendTail(buffer);
        return ChatColor.translateAlternateColorCodes('&', buffer.toString());
    }

    public static String getPrefix() {
        if (messagesConfig == null) {
            return "";
        }
        String prefix = messagesConfig.getString("prefix");
        return prefix != null ? colorize(prefix) : "";
    }

    public static String get(String key) {
        return get(key, Collections.emptyMap());
    }

    public static String get(String key, Map<String, String> replacements) {
        if (messagesConfig == null) {
            return "";
        }
        String message = messagesConfig.getString(key);
        if (message == null) {
            return "";
        }
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            if (entry.getKey() != null && entry.getValue() != null) {
                message = message.replace(entry.getKey(), entry.getValue());
            }
        }
        return colorize(message);
    }

    public static void send(CommandSender sender, String key) {
        send(sender, key, Collections.emptyMap());
    }

    public static void send(CommandSender sender, String key, Map<String, String> replacements) {
        String message = get(key, replacements);
        if (!message.isEmpty()) {
            sender.sendMessage(getPrefix() + message);
        }
    }

    public static void sendRaw(CommandSender sender, String key, Map<String, String> replacements) {
        String message = get(key, replacements);
        if (!message.isEmpty()) {
            sender.sendMessage(message);
        }
    }
}
