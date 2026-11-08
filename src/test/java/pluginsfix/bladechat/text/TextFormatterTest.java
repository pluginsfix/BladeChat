package pluginsfix.bladechat.text;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TextFormatterTest {

    @Test
    void shouldFormatHexAndLegacyColors() {
        String input = "&#E0A602Ⓛ &fPlayer &7⇨ &#D8D8D8Message";
        Component component = TextFormatter.format(input);

        assertThat(component).isNotNull();
    }

    @Test
    void shouldTranslateHexProperly() {
        String input = "&#E0A602Hello";
        String translated = TextFormatter.translateHex(input);

        assertThat(translated).isEqualTo("&x&E&0&A&6&0&2Hello");
    }

    @Test
    void shouldStripColors() {
        String input = "&#E0A602Hello &cWorld";
        String stripped = TextFormatter.stripColors(input);

        assertThat(stripped).isEqualTo("Hello World");
    }
}
