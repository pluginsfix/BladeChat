package pluginsfix.bladechat.config;

import org.bukkit.configuration.file.FileConfiguration;

public record ChatConfig(
        String localMessageFormat,
        String globalMessageFormat,
        double localRadius,
        String globalPrefix,
        int globalCooldownSeconds,
        boolean globalPermissionRequired,
        boolean allowColorCodes,
        String colorPermission,
        boolean spyConsole,
        String spyFormat
) {
    public static ChatConfig fromYaml(FileConfiguration config) {
        return new ChatConfig(
                config.getString("local-message-format"),
                config.getString("global-message-format"),
                config.getDouble("local-radius"),
                config.getString("global-prefix"),
                config.getInt("global-cooldown-seconds"),
                config.getBoolean("global-permission-required"),
                config.getBoolean("allow-color-codes"),
                config.getString("color-permission"),
                config.getBoolean("spy-console"),
                config.getString("spy-format")
        );
    }
}
