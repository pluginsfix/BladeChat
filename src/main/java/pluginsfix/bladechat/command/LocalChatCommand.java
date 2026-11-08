package pluginsfix.bladechat.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pluginsfix.bladechat.service.ChatService;
import pluginsfix.bladechat.text.Messages;

import java.util.Collections;
import java.util.List;

public final class LocalChatCommand implements CommandExecutor, TabCompleter {
    private final Messages messages;
    private final ChatService chatService;

    public LocalChatCommand(Messages messages, ChatService chatService) {
        this.messages = messages;
        this.chatService = chatService;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            messages.send(sender, "player-only");
            return true;
        }

        if (args.length == 0) {
            messages.send(player, "empty-message");
            return true;
        }

        String message = String.join(" ", args);
        chatService.sendLocalMessage(player, message);
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return Collections.emptyList();
    }
}
