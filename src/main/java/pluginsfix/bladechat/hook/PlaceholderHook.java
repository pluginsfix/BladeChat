package pluginsfix.bladechat.hook;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class PlaceholderHook {
    private final boolean enabled;

    public PlaceholderHook() {
        this.enabled = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String parse(Player player, String text) {
        if (!enabled || text == null || text.isEmpty()) {
            return text;
        }
        return PlaceholderAPI.setPlaceholders(player, text);
    }
}
