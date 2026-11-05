package org.bladestudio.bladechat.command;

import org.bladestudio.bladechat.BladeChat;
import org.bladestudio.bladechat.util.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class BladeChatCommand implements CommandExecutor, TabCompleter {

    private final BladeChat plugin;

    public BladeChatCommand(BladeChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            sendHelp(sender, label);
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("bladechat.reload") && !sender.hasPermission("bladechat.admin")) {
                Messages.send(sender, "no-permission");
                return true;
            }
            plugin.reloadPlugin();
            Messages.send(sender, "reload");
            return true;
        }

        if (args[0].equalsIgnoreCase("clear")) {
            if (!sender.hasPermission("bladechat.clear") && !sender.hasPermission("bladechat.admin")) {
                Messages.send(sender, "no-permission");
                return true;
            }
            String blank = " \n".repeat(100);
            Bukkit.broadcastMessage(blank);
            String senderName = (sender instanceof Player player) ? player.getName() : "Консоль";
            for (Player player : Bukkit.getOnlinePlayers()) {
                Messages.send(player, "clear", Map.of("%player%", senderName));
            }
            return true;
        }

        Messages.send(sender, "wrong-usage", Map.of("%cmd%", label));
        return true;
    }

    private void sendHelp(CommandSender sender, String label) {
        Messages.sendRaw(sender, "help.header", Collections.emptyMap());
        Messages.sendRaw(sender, "help.reload", Map.of("%cmd%", label));
        Messages.sendRaw(sender, "help.clear", Map.of("%cmd%", label));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            String current = args[0].toLowerCase();
            if ("reload".startsWith(current) && (sender.hasPermission("bladechat.reload") || sender.hasPermission("bladechat.admin"))) {
                completions.add("reload");
            }
            if ("clear".startsWith(current) && (sender.hasPermission("bladechat.clear") || sender.hasPermission("bladechat.admin"))) {
                completions.add("clear");
            }
            if ("help".startsWith(current)) {
                completions.add("help");
            }
            return completions;
        }
        return Collections.emptyList();
    }
}
