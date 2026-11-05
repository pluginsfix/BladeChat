package org.bladestudio.bladechat;

import org.bladestudio.bladechat.command.BladeChatCommand;
import org.bladestudio.bladechat.hook.HookManager;
import org.bladestudio.bladechat.listener.ChatListener;
import org.bladestudio.bladechat.listener.priority.LOWEST;
import org.bladestudio.bladechat.util.Messages;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public final class BladeChat extends JavaPlugin {

    private static BladeChat instance;
    private HookManager hookManager;
    private ChatListener currentListener;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        Messages.init(this);

        this.hookManager = new HookManager(this);

        registerChatListener();
        registerCommands();

        getLogger().info("BladeChat v" + getDescription().getVersion() + " успешно запущен!");
    }

    @Override
    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);
        if (currentListener != null) {
            HandlerList.unregisterAll(currentListener);
            currentListener = null;
        }
        getLogger().info("BladeChat выключен.");
        instance = null;
    }

    public void reloadPlugin() {
        reloadConfig();
        Messages.reload();
        registerChatListener();
    }

    private void registerChatListener() {
        if (currentListener != null) {
            HandlerList.unregisterAll(currentListener);
        }
        String priorityName = getConfig().getString("priority", "LOWEST").toUpperCase();
        try {
            Class<?> clazz = Class.forName("org.bladestudio.bladechat.listener.priority." + priorityName);
            this.currentListener = (ChatListener) clazz.getDeclaredConstructor(BladeChat.class).newInstance(this);
            getServer().getPluginManager().registerEvents(this.currentListener, this);
        } catch (Exception ex) {
            this.currentListener = new LOWEST(this);
            getServer().getPluginManager().registerEvents(this.currentListener, this);
        }
    }

    private void registerCommands() {
        PluginCommand command = getCommand("bladechat");
        if (command != null) {
            BladeChatCommand executor = new BladeChatCommand(this);
            command.setExecutor(executor);
            command.setTabCompleter(executor);
        }
    }

    public static BladeChat getInstance() {
        return instance;
    }

    public HookManager getHookManager() {
        return hookManager;
    }
}
