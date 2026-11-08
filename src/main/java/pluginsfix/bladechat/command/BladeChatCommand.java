package pluginsfix.bladechat.command;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pluginsfix.bladechat.BladeChat;
import pluginsfix.bladechat.service.ChatService;
import pluginsfix.bladechat.text.Messages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class BladeChatCommand implements CommandExecutor, TabCompleter {
    private static final Component CLEAR_BLOCK = Component.text("\n".repeat(100));
    private final BladeChat plugin;
    private final Messages messages;
    private final ChatService chatService;

    public BladeChatCommand(BladeChat plugin, Messages messages, ChatService chatService) {
        this.plugin = plugin;
        this.messages = messages;
        this.chatService = chatService;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("bladechat.use")) {
            messages.send(sender, "no-permission");
            return true;
        }

        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            messages.sendList(sender, "help");
            return true;
        }

        String sub = args[0].toLowerCase();
        switch (sub) {
            case "reload" -> {
                if (!sender.hasPermission("bladechat.admin.reload")) {
                    messages.send(sender, "no-permission");
                    return true;
                }
                plugin.reloadPlugin();
                messages.send(sender, "reloaded");
            }
            case "clear" -> {
                if (!sender.hasPermission("bladechat.admin.clear")) {
                    messages.send(sender, "no-permission");
                    return true;
                }
                for (Player online : Bukkit.getOnlinePlayers()) {
                    if (!online.hasPermission("bladechat.admin.clear.bypass")) {
                        online.sendMessage(CLEAR_BLOCK);
                    }
                }
                if (sender instanceof Player player) {
                    Bukkit.broadcast(messages.get("chat-cleared", "%player%", player.getName()));
                } else {
                    Bukkit.broadcast(messages.get("chat-cleared-console"));
                }
            }
            case "mute", "lock" -> {
                if (!sender.hasPermission("bladechat.admin.mute")) {
                    messages.send(sender, "no-permission");
                    return true;
                }
                boolean muted = chatService.toggleChatMuted();
                if (muted) {
                    Bukkit.broadcast(messages.get("chat-muted"));
                } else {
                    Bukkit.broadcast(messages.get("chat-unmuted"));
                }
            }
            case "spy" -> {
                if (!(sender instanceof Player player)) {
                    messages.send(sender, "player-only");
                    return true;
                }
                if (!player.hasPermission("bladechat.admin.spy")) {
                    messages.send(player, "no-permission");
                    return true;
                }
                boolean enabled = chatService.toggleSpy(player.getUniqueId());
                if (enabled) {
                    messages.send(player, "spy-enabled");
                } else {
                    messages.send(player, "spy-disabled");
                }
            }
            default -> messages.sendList(sender, "help");
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            if (sender.hasPermission("bladechat.admin.reload")) completions.add("reload");
            if (sender.hasPermission("bladechat.admin.clear")) completions.add("clear");
            if (sender.hasPermission("bladechat.admin.mute")) completions.add("mute");
            if (sender.hasPermission("bladechat.admin.spy") && sender instanceof Player) completions.add("spy");
            completions.add("help");

            String prefix = args[0].toLowerCase();
            return completions.stream()
                    .filter(s -> s.startsWith(prefix))
                    .toList();
        }
        return Collections.emptyList();
    }
}
