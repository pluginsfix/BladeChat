package pluginsfix.bladechat.hook;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class LuckPermsHook {
    private final boolean enabled;

    public LuckPermsHook() {
        this.enabled = Bukkit.getPluginManager().isPluginEnabled("LuckPerms");
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getPrefix(Player player) {
        if (!enabled) {
            return "";
        }
        try {
            LuckPerms lp = LuckPermsProvider.get();
            User user = lp.getUserManager().getUser(player.getUniqueId());
            if (user == null) {
                return "";
            }
            String prefix = user.getCachedData().getMetaData().getPrefix();
            return prefix != null ? prefix : "";
        } catch (Exception ignored) {
            return "";
        }
    }

    public String getSuffix(Player player) {
        if (!enabled) {
            return "";
        }
        try {
            LuckPerms lp = LuckPermsProvider.get();
            User user = lp.getUserManager().getUser(player.getUniqueId());
            if (user == null) {
                return "";
            }
            String suffix = user.getCachedData().getMetaData().getSuffix();
            return suffix != null ? suffix : "";
        } catch (Exception ignored) {
            return "";
        }
    }
}
