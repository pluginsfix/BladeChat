package org.bladestudio.bladechat.util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Collections;
import java.util.Map;
import java.util.regex.Pattern;

public final class Messages {

    private static final Pattern ITALIC_PATTERN = Pattern.compile("(?i)[&§]o");
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
        if (text == null) {
            return "";
        }
        String cleaned = ITALIC_PATTERN.matcher(text).replaceAll("");
        return ChatColor.translateAlternateColorCodes('&', cleaned);
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
