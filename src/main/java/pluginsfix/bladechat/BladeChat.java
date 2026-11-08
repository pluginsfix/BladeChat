package pluginsfix.bladechat;

import org.bukkit.command.PluginCommand;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import pluginsfix.bladechat.command.BladeChatCommand;
import pluginsfix.bladechat.command.GlobalChatCommand;
import pluginsfix.bladechat.command.LocalChatCommand;
import pluginsfix.bladechat.config.ChatConfig;
import pluginsfix.bladechat.hook.LuckPermsHook;
import pluginsfix.bladechat.hook.PlaceholderHook;
import pluginsfix.bladechat.listener.ChatListener;
import pluginsfix.bladechat.service.ChatService;
import pluginsfix.bladechat.text.Messages;

public final class BladeChat extends JavaPlugin {
    private ChatConfig chatConfig;
    private Messages messages;
    private PlaceholderHook placeholderHook;
    private LuckPermsHook luckPermsHook;
    private ChatService chatService;
    private ChatListener chatListener;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.chatConfig = ChatConfig.fromYaml(getConfig());
        this.messages = new Messages(this);
        this.placeholderHook = new PlaceholderHook();
        this.luckPermsHook = new LuckPermsHook();
        this.chatService = new ChatService(chatConfig, messages, placeholderHook, luckPermsHook);

        this.chatListener = new ChatListener(messages, chatService);
        getServer().getPluginManager().registerEvents(chatListener, this);

        registerCommands();
    }

    private void registerCommands() {
        PluginCommand mainCommand = getCommand("bladechat");
        if (mainCommand != null) {
            BladeChatCommand executor = new BladeChatCommand(this, messages, chatService);
            mainCommand.setExecutor(executor);
            mainCommand.setTabCompleter(executor);
        }

        PluginCommand globalCommand = getCommand("g");
        if (globalCommand != null) {
            GlobalChatCommand executor = new GlobalChatCommand(messages, chatService);
            globalCommand.setExecutor(executor);
            globalCommand.setTabCompleter(executor);
        }

        PluginCommand localCommand = getCommand("l");
        if (localCommand != null) {
            LocalChatCommand executor = new LocalChatCommand(messages, chatService);
            localCommand.setExecutor(executor);
            localCommand.setTabCompleter(executor);
        }
    }

    public void reloadPlugin() {
        reloadConfig();
        this.chatConfig = ChatConfig.fromYaml(getConfig());
        this.messages.reload();
        this.chatService.updateConfig(chatConfig);
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
    }

    public ChatConfig getChatConfig() {
        return chatConfig;
    }

    public Messages getMessages() {
        return messages;
    }

    public ChatService getChatService() {
        return chatService;
    }
}
