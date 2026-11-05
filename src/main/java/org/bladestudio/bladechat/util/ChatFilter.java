package org.bladestudio.bladechat.util;

import org.bukkit.entity.Player;

import java.util.List;
import java.util.regex.Pattern;

public final class ChatFilter {

    private static final Pattern CHAT_COLOR_PATTERN = Pattern.compile("(?i)&([0-9a-f])");
    private static final Pattern CHAT_MAGIC_PATTERN = Pattern.compile("(?i)&([k])");
    private static final Pattern CHAT_BOLD_PATTERN = Pattern.compile("(?i)&([l])");
    private static final Pattern CHAT_STRIKETHROUGH_PATTERN = Pattern.compile("(?i)&([m])");
    private static final Pattern CHAT_UNDERLINE_PATTERN = Pattern.compile("(?i)&([n])");
    private static final Pattern CHAT_ITALIC_PATTERN = Pattern.compile("(?i)&([o])");
    private static final Pattern CHAT_RESET_PATTERN = Pattern.compile("(?i)&([r])");

    private static final Pattern IP_PATTERN = Pattern.compile(
            "((?<![0-9])(?:(?:25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})[ ]?[.,-:; ][ ]?(?:25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})[ ]?[., ][ ]?(?:25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})[ ]?[., ][ ]?(?:25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2}))(?![0-9]))"
    );
    private static final Pattern WEB_PATTERN = Pattern.compile(
            "[-a-zA-Z0-9@:%_\\+.~#?&/=]{2,256}\\.[a-z]{2,4}\\b(/[-a-zA-Z0-9@:%_\\+~#?&/=]*)?"
    );

    private ChatFilter() {}

    public static String filterPlayerMessage(String message, Player player) {
        if (message == null || message.isEmpty()) {
            return "";
        }
        String formatted = message;

        if (player.hasPermission("bladechat.chat.color")) {
            formatted = CHAT_COLOR_PATTERN.matcher(formatted).replaceAll("§$1");
        } else {
            formatted = formatted.replaceAll("(?i)[&§][0-9a-f]", "");
        }

        if (player.hasPermission("bladechat.chat.magic")) {
            formatted = CHAT_MAGIC_PATTERN.matcher(formatted).replaceAll("§$1");
        } else {
            formatted = formatted.replaceAll("(?i)[&§]k", "");
        }

        if (player.hasPermission("bladechat.chat.bold")) {
            formatted = CHAT_BOLD_PATTERN.matcher(formatted).replaceAll("§$1");
        } else {
            formatted = formatted.replaceAll("(?i)[&§]l", "");
        }

        if (player.hasPermission("bladechat.chat.strikethrough")) {
            formatted = CHAT_STRIKETHROUGH_PATTERN.matcher(formatted).replaceAll("§$1");
        } else {
            formatted = formatted.replaceAll("(?i)[&§]m", "");
        }

        if (player.hasPermission("bladechat.chat.underline")) {
            formatted = CHAT_UNDERLINE_PATTERN.matcher(formatted).replaceAll("§$1");
        } else {
            formatted = formatted.replaceAll("(?i)[&§]n", "");
        }

        if (player.hasPermission("bladechat.chat.italic")) {
            formatted = CHAT_ITALIC_PATTERN.matcher(formatted).replaceAll("§$1");
        } else {
            formatted = formatted.replaceAll("(?i)[&§]o", "");
        }

        if (player.hasPermission("bladechat.chat.reset")) {
            formatted = CHAT_RESET_PATTERN.matcher(formatted).replaceAll("§$1");
        } else {
            formatted = formatted.replaceAll("(?i)[&§]r", "");
        }

        return formatted;
    }

    public static boolean containsAdvertisement(String text, List<String> whitelist) {
        if (text == null || text.isEmpty()) {
            return false;
        }

        String normalized = text.toLowerCase();
        for (String allowed : whitelist) {
            if (allowed != null && !allowed.isEmpty()) {
                normalized = normalized.replace(allowed.toLowerCase(), "");
            }
        }

        String compact = normalized.replaceAll("[\\s_\\-,;:]+", "");

        if (IP_PATTERN.matcher(normalized).find() || IP_PATTERN.matcher(compact).find()) {
            return true;
        }

        return WEB_PATTERN.matcher(normalized).find() || WEB_PATTERN.matcher(compact).find();
    }
}
