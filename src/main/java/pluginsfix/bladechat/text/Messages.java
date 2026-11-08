package pluginsfix.bladechat.text;

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.List;

public final class Messages {
    private final Plugin plugin;
    private final File file;
    private YamlConfiguration config;

    public Messages(Plugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "messages.yml");
        load();
    }

    public void load() {
        if (!file.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    public void reload() {
        load();
    }

    public String getRaw(String key) {
        String value = config.getString(key);
        if (value == null) {
            return key;
        }
        return value;
    }

    public Component get(String key) {
        return TextFormatter.format(getRaw(key));
    }

    public Component get(String key, String... replacements) {
        String raw = getRaw(key);
        for (int i = 0; i < replacements.length; i += 2) {
            if (i + 1 < replacements.length) {
                raw = raw.replace(replacements[i], replacements[i + 1]);
            }
        }
        return TextFormatter.format(raw);
    }

    public void send(CommandSender sender, String key) {
        sender.sendMessage(get(key));
    }

    public void send(CommandSender sender, String key, String... replacements) {
        sender.sendMessage(get(key, replacements));
    }

    public void sendList(CommandSender sender, String key) {
        List<String> lines = config.getStringList(key);
        for (String line : lines) {
            sender.sendMessage(TextFormatter.format(line));
        }
    }
}
