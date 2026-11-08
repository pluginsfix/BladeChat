package pluginsfix.bladechat.service;

import org.junit.jupiter.api.Test;
import pluginsfix.bladechat.config.ChatConfig;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ChatServiceTest {

    @Test
    void shouldToggleChatMuted() {
        ChatConfig config = new ChatConfig(
                "local-format",
                "global-format",
                100.0,
                "!",
                3,
                false,
                true,
                "bladechat.color",
                true,
                "spy-format"
        );
        ChatService service = new ChatService(config, null, null, null);

        assertThat(service.isChatMuted()).isFalse();
        assertThat(service.toggleChatMuted()).isTrue();
        assertThat(service.isChatMuted()).isTrue();
        assertThat(service.toggleChatMuted()).isFalse();
        assertThat(service.isChatMuted()).isFalse();
    }

    @Test
    void shouldToggleSpyMode() {
        ChatConfig config = new ChatConfig(
                "local-format",
                "global-format",
                100.0,
                "!",
                3,
                false,
                true,
                "bladechat.color",
                true,
                "spy-format"
        );
        ChatService service = new ChatService(config, null, null, null);
        UUID uuid = UUID.randomUUID();

        assertThat(service.isSpy(uuid)).isFalse();
        assertThat(service.toggleSpy(uuid)).isTrue();
        assertThat(service.isSpy(uuid)).isTrue();
        assertThat(service.toggleSpy(uuid)).isFalse();
        assertThat(service.isSpy(uuid)).isFalse();
    }

    @Test
    void shouldSwitchChatChannels() {
        ChatConfig config = new ChatConfig(
                "local-format",
                "global-format",
                100.0,
                "!",
                3,
                false,
                true,
                "bladechat.color",
                true,
                "spy-format"
        );
        ChatService service = new ChatService(config, null, null, null);
        UUID uuid = UUID.randomUUID();

        assertThat(service.getChannel(uuid)).isEqualTo(ChatChannel.LOCAL);
        service.setChannel(uuid, ChatChannel.GLOBAL);
        assertThat(service.getChannel(uuid)).isEqualTo(ChatChannel.GLOBAL);
        service.setChannel(uuid, ChatChannel.LOCAL);
        assertThat(service.getChannel(uuid)).isEqualTo(ChatChannel.LOCAL);
    }
}
